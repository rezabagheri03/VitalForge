import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers

# 1. Load and normalize data
data = np.load('vital_features.npy')  # shape (samples, 4)
mean, std = data.mean(axis=0), data.std(axis=0)
data_norm = (data - mean) / std

# 2. Split into train and validation
split = int(0.8 * len(data_norm))
train_data, val_data = data_norm[:split], data_norm[split:]

# 3. Build autoencoder model
def build_autoencoder(input_dim):
    inp = keras.Input(shape=(input_dim,))
    x = layers.Dense(32, activation='relu')(inp)
    x = layers.Dense(16, activation='relu')(x)
    bottleneck = layers.Dense(8, activation='relu')(x)
    x = layers.Dense(16, activation='relu')(bottleneck)
    x = layers.Dense(32, activation='relu')(x)
    out = layers.Dense(input_dim, activation='linear')(x)
    return keras.Model(inp, out)

ae = build_autoencoder(4)
ae.compile(optimizer='adam', loss='mse')
ae.fit(
    train_data, train_data,
    epochs=50,
    batch_size=32,
    validation_data=(val_data, val_data)
)

# 4. Save full-precision model
ae.save('anomaly_autoencoder.h5')

# 5. Convert to quantized TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(ae)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
def rep_dataset():
    for i in range(100):
        yield [train_data[i:i+1].astype(np.float32)]
converter.representative_dataset = rep_dataset
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
converter.inference_input_type = tf.int8
converter.inference_output_type = tf.int8
tflite_model = converter.convert()
with open('anomaly_autoencoder_quant.tflite', 'wb') as f:
    f.write(tflite_model)

# 6. Save normalization parameters
np.savez('anomaly_norm_params.npz', mean=mean, std=std)
