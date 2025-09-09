#!/bin/bash

# VitalForge Model Deployment Script
# This script trains and deploys the TensorFlow Lite models

echo "=== VitalForge Model Deployment ==="

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo "Python 3 is required but not installed."
    exit 1
fi

# Install requirements
echo "Installing Python requirements..."
pip3 install -r requirements.txt

# Run the training pipeline
echo "Starting model training..."
python3 train_and_deploy_models.py

# Check if models were created successfully
if [ -f "app/src/main/assets/anomaly_autoencoder_quant.tflite" ]; then
    echo "✅ TensorFlow Lite model deployed successfully"
else
    echo "❌ Failed to deploy TensorFlow Lite model"
    exit 1
fi

if [ -f "app/src/main/assets/anomaly_norm_params.npz" ]; then
    echo "✅ Normalization parameters deployed successfully"
else
    echo "❌ Failed to deploy normalization parameters"
    exit 1
fi

if [ -f "app/src/main/assets/normal_vitals.npy" ]; then
    echo "✅ Calibration data deployed successfully"
else
    echo "❌ Failed to deploy calibration data"
    exit 1
fi

echo ""
echo "🎉 All models deployed successfully!"
echo ""
echo "Next steps:"
echo "1. Build the Android project: ./gradlew build"
echo "2. Test on device/emulator"
echo "3. Collect real data for model improvement"