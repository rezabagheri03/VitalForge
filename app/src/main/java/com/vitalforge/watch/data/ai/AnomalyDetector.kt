package com.vitalforge.watch.data.ai

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnomalyDetector @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "AnomalyDetector"
        private const val MODEL_FILE = "anomaly_autoencoder_quant.tflite"
        private const val PARAMS_FILE = "anomaly_norm_params.npz"
        private const val CALIB_FILE = "normal_vitals.npy"
        private const val INPUT_DIM = 4
    }

    private val interpreter: Interpreter
    private val mean: FloatArray
    private val std: FloatArray
    private val threshold: Float

    init {
        // Load normalization parameters
        val params = NpzLoader.load(context.assets.open(PARAMS_FILE))
        mean = params["mean"]!!
        std = params["std"]!!

        // Initialize TFLite interpreter
        val gpuDelegate = GpuDelegate()
        interpreter = Interpreter(loadModel(MODEL_FILE), Interpreter.Options().addDelegate(gpuDelegate))

        // Calibrate threshold
        threshold = calibrateThreshold()
        Log.d(TAG, "Calibrated threshold: $threshold")
    }

    fun detectAnomaly(features: FloatArray): Pair<Boolean, Float> {
        val inputBuffer = ByteBuffer.allocateDirect(INPUT_DIM)
            .order(ByteOrder.nativeOrder())
        for (i in 0 until INPUT_DIM) {
            val norm = (features[i] - mean[i]) / std[i]
            inputBuffer.put((norm * 128).toInt().toByte())
        }
        inputBuffer.rewind()

        val outputBuffer = ByteBuffer.allocateDirect(INPUT_DIM)
            .order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()

        var mse = 0f
        for (i in 0 until INPUT_DIM) {
            val reconQ = outputBuffer.get().toInt()
            val recon = reconQ / 128f * std[i] + mean[i]
            val err = recon - features[i]
            mse += err * err
        }
        mse /= INPUT_DIM
        return Pair(mse > threshold, mse)
    }

    private fun calibrateThreshold(): Float {
        val samples = NpyLoader.load(context.assets.open(CALIB_FILE))
        val errors = samples.map { detectAnomaly(it.toFloatArray()).second }
        val meanErr = errors.average().toFloat()
        val stdErr = sqrt(errors.map { (it - meanErr) * (it - meanErr) }.average().toFloat())
        return meanErr + 3 * stdErr
    }

    private fun loadModel(file: String): ByteBuffer {
        val fd = context.assets.openFd(file)
        val bytes = ByteArray(fd.length.toInt())
        fd.createInputStream().read(bytes)
        return ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).apply { put(bytes); rewind() }
    }
}

// Utility loaders for .npz and .npy
object NpzLoader {
    fun load(input: InputStream): Map<String, FloatArray> {
        // For now, return default normalization parameters
        // In a real implementation, you would parse the .npz file
        return mapOf(
            "mean" to floatArrayOf(70.0f, 98.0f, 0.5f, 0.0f), // Default values for HR, SpO2, PPG, ECG
            "std" to floatArrayOf(15.0f, 2.0f, 0.2f, 1.0f)
        )
    }
}

object NpyLoader {
    fun load(input: InputStream): List<DoubleArray> {
        // For now, return sample calibration data
        // In a real implementation, you would parse the .npy file
        return listOf(
            doubleArrayOf(70.0, 98.0, 0.5, 0.0),
            doubleArrayOf(72.0, 97.0, 0.6, 0.1),
            doubleArrayOf(68.0, 99.0, 0.4, -0.1)
        )
    }
}
