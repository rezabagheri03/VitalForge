#!/usr/bin/env python3
"""
Complete pipeline to train and deploy TensorFlow Lite models for VitalForge.
This script handles the entire process from data preparation to model deployment.
"""

import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
import os
import shutil

def prepare_data():
    """Prepare training data if not exists"""
    if not os.path.exists('vital_features.npy'):
        print("Training data not found. Generating synthetic data...")
        os.system('python prepare_training_data.py')
    else:
        print("Training data found.")

def build_autoencoder(input_dim=4):
    """Build the autoencoder model"""
    inp = keras.Input(shape=(input_dim,))
    
    # Encoder
    x = layers.Dense(32, activation='relu')(inp)
    x = layers.Dropout(0.1)(x)
    x = layers.Dense(16, activation='relu')(x)
    x = layers.Dropout(0.1)(x)
    bottleneck = layers.Dense(8, activation='relu')(x)
    
    # Decoder
    x = layers.Dense(16, activation='relu')(bottleneck)
    x = layers.Dropout(0.1)(x)
    x = layers.Dense(32, activation='relu')(x)
    x = layers.Dropout(0.1)(x)
    out = layers.Dense(input_dim, activation='linear')(x)
    
    model = keras.Model(inp, out)
    return model

def train_model():
    """Train the autoencoder model"""
    print("Loading training data...")
    data = np.load('vital_features.npy')
    
    # Normalize data
    mean, std = data.mean(axis=0), data.std(axis=0)
    data_norm = (data - mean) / std
    
    # Split data
    split = int(0.8 * len(data_norm))
    train_data, val_data = data_norm[:split], data_norm[split:]
    
    print(f"Training samples: {len(train_data)}")
    print(f"Validation samples: {len(val_data)}")
    
    # Build model
    model = build_autoencoder(4)
    model.compile(optimizer='adam', loss='mse', metrics=['mae'])
    
    # Print model summary
    model.summary()
    
    # Train model
    print("Training model...")
    history = model.fit(
        train_data, train_data,
        epochs=100,
        batch_size=32,
        validation_data=(val_data, val_data),
        callbacks=[
            keras.callbacks.EarlyStopping(patience=10, restore_best_weights=True),
            keras.callbacks.ReduceLROnPlateau(factor=0.5, patience=5)
        ]
    )
    
    # Save full-precision model
    model.save('anomaly_autoencoder.h5')
    print("Model saved as anomaly_autoencoder.h5")
    
    return model, mean, std

def convert_to_tflite(model, mean, std):
    """Convert model to TensorFlow Lite"""
    print("Converting to TensorFlow Lite...")
    
    # Load calibration data
    normal_data = np.load('normal_vitals.npy')
    mean_norm, std_norm = normal_data.mean(axis=0), normal_data.std(axis=0)
    calib_data = (normal_data - mean_norm) / std_norm
    
    # Convert to TFLite with quantization
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    
    # Representative dataset for quantization
    def representative_dataset():
        for i in range(min(100, len(calib_data))):
            yield [calib_data[i:i+1].astype(np.float32)]
    
    converter.representative_dataset = representative_dataset
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
    converter.inference_input_type = tf.int8
    converter.inference_output_type = tf.int8
    
    # Convert
    tflite_model = converter.convert()
    
    # Save quantized model
    with open('anomaly_autoencoder_quant.tflite', 'wb') as f:
        f.write(tflite_model)
    
    print("Quantized TFLite model saved as anomaly_autoencoder_quant.tflite")
    
    return calib_data

def save_normalization_params(mean, std):
    """Save normalization parameters"""
    print("Saving normalization parameters...")
    
    # Save as NPZ file
    np.savez('anomaly_norm_params.npz', mean=mean, std=std)
    
    # Also save as individual files for easier loading
    np.save('normalization_mean.npy', mean)
    np.save('normalization_std.npy', std)
    
    print("Normalization parameters saved")

def deploy_to_android():
    """Deploy models to Android assets directory"""
    print("Deploying models to Android...")
    
    # Create assets directory if it doesn't exist
    assets_dir = 'app/src/main/assets'
    os.makedirs(assets_dir, exist_ok=True)
    
    # Copy model files
    files_to_copy = [
        'anomaly_autoencoder_quant.tflite',
        'anomaly_norm_params.npz',
        'normal_vitals.npy'
    ]
    
    for file in files_to_copy:
        if os.path.exists(file):
            shutil.copy2(file, assets_dir)
            print(f"Copied {file} to {assets_dir}")
        else:
            print(f"Warning: {file} not found")

def validate_model():
    """Validate the trained model"""
    print("Validating model...")
    
    # Load the model
    model = keras.models.load_model('anomaly_autoencoder.h5')
    
    # Load test data
    data = np.load('vital_features.npy')
    mean, std = data.mean(axis=0), data.std(axis=0)
    data_norm = (data - mean) / std
    
    # Test on a few samples
    test_samples = data_norm[:10]
    predictions = model.predict(test_samples)
    
    # Calculate reconstruction error
    mse = np.mean((test_samples - predictions) ** 2, axis=1)
    print(f"Average reconstruction error: {np.mean(mse):.4f}")
    print(f"Max reconstruction error: {np.max(mse):.4f}")
    print(f"Min reconstruction error: {np.min(mse):.4f}")

def main():
    """Main training and deployment pipeline"""
    print("=== VitalForge Model Training Pipeline ===")
    
    # Step 1: Prepare data
    prepare_data()
    
    # Step 2: Train model
    model, mean, std = train_model()
    
    # Step 3: Convert to TFLite
    calib_data = convert_to_tflite(model, mean, std)
    
    # Step 4: Save normalization parameters
    save_normalization_params(mean, std)
    
    # Step 5: Deploy to Android
    deploy_to_android()
    
    # Step 6: Validate model
    validate_model()
    
    print("\n=== Training Complete ===")
    print("Models are ready for deployment in VitalForge!")
    print("\nNext steps:")
    print("1. Build the Android project")
    print("2. Test the models on device")
    print("3. Fine-tune based on real data")

if __name__ == "__main__":
    main()