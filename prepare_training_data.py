#!/usr/bin/env python3
"""
Script to prepare training data for VitalForge anomaly detection model.
This script generates synthetic vital signs data for training the autoencoder.
"""

import numpy as np
import pandas as pd
from scipy import stats
import matplotlib.pyplot as plt

def generate_normal_vitals(n_samples=10000):
    """Generate normal vital signs data"""
    np.random.seed(42)
    
    # Normal ranges for vital signs
    # Heart Rate: 60-100 bpm
    # SpO2: 95-100%
    # PPG: 0.3-0.7 (normalized)
    # ECG: -0.5 to 0.5 (normalized)
    
    heart_rate = np.random.normal(75, 10, n_samples)
    heart_rate = np.clip(heart_rate, 60, 100)
    
    spo2 = np.random.normal(98, 1.5, n_samples)
    spo2 = np.clip(spo2, 95, 100)
    
    ppg = np.random.normal(0.5, 0.1, n_samples)
    ppg = np.clip(ppg, 0.3, 0.7)
    
    ecg = np.random.normal(0, 0.2, n_samples)
    ecg = np.clip(ecg, -0.5, 0.5)
    
    return np.column_stack([heart_rate, spo2, ppg, ecg])

def generate_anomalous_vitals(n_samples=1000):
    """Generate anomalous vital signs data"""
    np.random.seed(123)
    
    # Anomalous patterns
    anomalies = []
    
    # High heart rate episodes
    hr_high = np.random.normal(120, 15, n_samples//4)
    hr_high = np.clip(hr_high, 100, 150)
    spo2_normal = np.random.normal(98, 1.5, n_samples//4)
    ppg_normal = np.random.normal(0.5, 0.1, n_samples//4)
    ecg_normal = np.random.normal(0, 0.2, n_samples//4)
    anomalies.append(np.column_stack([hr_high, spo2_normal, ppg_normal, ecg_normal]))
    
    # Low SpO2 episodes
    hr_normal = np.random.normal(75, 10, n_samples//4)
    spo2_low = np.random.normal(90, 3, n_samples//4)
    spo2_low = np.clip(spo2_low, 85, 95)
    ppg_normal = np.random.normal(0.5, 0.1, n_samples//4)
    ecg_normal = np.random.normal(0, 0.2, n_samples//4)
    anomalies.append(np.column_stack([hr_normal, spo2_low, ppg_normal, ecg_normal]))
    
    # Irregular PPG patterns
    hr_normal = np.random.normal(75, 10, n_samples//4)
    spo2_normal = np.random.normal(98, 1.5, n_samples//4)
    ppg_irregular = np.random.normal(0.3, 0.2, n_samples//4)
    ppg_irregular = np.clip(ppg_irregular, 0.1, 0.9)
    ecg_normal = np.random.normal(0, 0.2, n_samples//4)
    anomalies.append(np.column_stack([hr_normal, spo2_normal, ppg_irregular, ecg_normal]))
    
    # ECG artifacts
    hr_normal = np.random.normal(75, 10, n_samples//4)
    spo2_normal = np.random.normal(98, 1.5, n_samples//4)
    ppg_normal = np.random.normal(0.5, 0.1, n_samples//4)
    ecg_artifact = np.random.normal(0, 0.8, n_samples//4)
    ecg_artifact = np.clip(ecg_artifact, -1.5, 1.5)
    anomalies.append(np.column_stack([hr_normal, spo2_normal, ppg_normal, ecg_artifact]))
    
    return np.vstack(anomalies)

def main():
    """Generate and save training data"""
    print("Generating training data...")
    
    # Generate normal data
    normal_data = generate_normal_vitals(10000)
    print(f"Generated {len(normal_data)} normal samples")
    
    # Generate anomalous data
    anomalous_data = generate_anomalous_vitals(1000)
    print(f"Generated {len(anomalous_data)} anomalous samples")
    
    # Combine all data
    all_data = np.vstack([normal_data, anomalous_data])
    
    # Shuffle the data
    np.random.shuffle(all_data)
    
    # Save the data
    np.save('vital_features.npy', all_data)
    np.save('normal_vitals.npy', normal_data)
    
    print("Training data saved:")
    print(f"- vital_features.npy: {all_data.shape}")
    print(f"- normal_vitals.npy: {normal_data.shape}")
    
    # Display statistics
    print("\nData Statistics:")
    print(f"Mean: {all_data.mean(axis=0)}")
    print(f"Std: {all_data.std(axis=0)}")
    print(f"Min: {all_data.min(axis=0)}")
    print(f"Max: {all_data.max(axis=0)}")

if __name__ == "__main__":
    main()