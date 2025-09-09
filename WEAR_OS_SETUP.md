# Wear OS Development Setup Guide

## Galaxy Watch 4+ Compatibility

Since Galaxy Watch 4 and newer models run **Wear OS** (not Tizen), VitalForge uses the standard Android/Wear OS ecosystem:

### Supported Devices:
- **Galaxy Watch 4** (Wear OS 3.0+)
- **Galaxy Watch 5** (Wear OS 3.5+)
- **Galaxy Watch 6** (Wear OS 4.0+)
- **Galaxy Watch 7** (Wear OS 4.0+)
- **Pixel Watch** (Wear OS 3.0+)
- **Other Wear OS devices**

## Health Services Integration

### Google Health Services
VitalForge uses Google Health Services for sensor access:

```kotlin
// Dependencies in build.gradle.kts
implementation("androidx.health:health-services-client:1.0.0-beta03")
implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
```

### Available Sensors:
- **Heart Rate**: Continuous monitoring
- **SpO2**: Blood oxygen saturation
- **ECG**: Single-lead electrocardiogram (Galaxy Watch 4+)
- **PPG**: Photoplethysmography data
- **Activity Recognition**: Steps, calories, etc.

## Development Setup

### 1. Enable Developer Options on Galaxy Watch

1. Go to **Settings** > **About** > **Software info**
2. Tap **Software version** 7 times
3. Go back to **Settings** > **Developer options**
4. Enable **ADB debugging**
5. Enable **Debug over Wi-Fi** (optional)

### 2. Connect Watch to Android Studio

```bash
# Connect via USB (if using USB debugging)
adb devices

# Connect via Wi-Fi (if using Wi-Fi debugging)
adb connect <watch-ip-address>:5555
```

### 3. Install Health Services

The Health Services app should be pre-installed on Galaxy Watch 4+, but you can verify:

```bash
# Check if Health Services is installed
adb shell pm list packages | grep health
```

### 4. Permissions Setup

Add required permissions to `AndroidManifest.xml`:

```xml
<!-- Health Services permissions -->
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<!-- Health Connect permissions -->
<uses-permission android:name="android.permission.health.READ_STEPS" />
<uses-permission android:name="android.permission.health.READ_HEART_RATE" />
<uses-permission android:name="android.permission.health.READ_OXYGEN_SATURATION" />
<uses-permission android:name="android.permission.health.READ_ECG" />
<uses-permission android:name="android.permission.health.WRITE_HEART_RATE" />
<uses-permission android:name="android.permission.health.WRITE_OXYGEN_SATURATION" />
```

## Testing on Device

### 1. Build and Install

```bash
# Build the Wear OS app
./gradlew :app:assembleDebug

# Install on connected watch
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. Grant Permissions

On the watch:
1. Open **Settings** > **Apps** > **VitalForge**
2. Go to **Permissions**
3. Grant all required permissions:
   - Body sensors
   - Activity recognition
   - Bluetooth
   - Health data access

### 3. Test Health Services

```kotlin
// Test code to verify health services
val healthServicesClient = HealthServicesClient.getOrCreate(context)
val capabilities = healthServicesClient.measureClient.getCapabilities()

Log.d("HealthServices", "Supported data types: ${capabilities.supportedDataTypes}")
Log.d("HealthServices", "Heart rate available: ${capabilities.supportedDataTypes.contains(DataType.HEART_RATE_BPM)}")
```

## Galaxy Watch Specific Features

### ECG Support
Galaxy Watch 4+ supports single-lead ECG:

```kotlin
// Check ECG availability
val capabilities = measureClient.getCapabilities()
val hasEcg = capabilities.supportedDataTypes.contains(DataType.ECG)

if (hasEcg) {
    // Start ECG monitoring
    val ecgRequest = MeasureRequest.builder(DataType.ECG)
        .setCallback(ecgCallback)
        .build()
    measureClient.startMeasure(ecgRequest)
}
```

### Advanced Health Metrics
Galaxy Watch 4+ provides additional metrics:
- **Heart Rate Variability (HRV)**
- **Blood Pressure** (Galaxy Watch 5+)
- **Body Composition** (Galaxy Watch 4+)
- **Sleep Tracking**

## Troubleshooting

### Common Issues:

1. **Health Services not available**
   ```bash
   # Check if Health Services is installed
   adb shell pm list packages | grep health
   
   # If missing, install from Play Store on watch
   ```

2. **Permissions denied**
   - Ensure all permissions are granted in watch settings
   - Check AndroidManifest.xml for required permissions

3. **Sensor data not received**
   - Verify device compatibility
   - Check Health Services capabilities
   - Ensure proper callback implementation

4. **Build errors**
   - Update Android Studio to latest version
   - Sync project with Gradle files
   - Clean and rebuild project

### Debug Commands:

```bash
# Check connected devices
adb devices

# View logs
adb logcat | grep VitalForge

# Check Health Services logs
adb logcat | grep HealthServices

# Monitor sensor data
adb logcat | grep "HeartRate\|SpO2\|ECG"
```

## Production Considerations

### 1. Battery Optimization
- Use passive monitoring for continuous data
- Implement proper data batching
- Optimize sensor sampling rates

### 2. Data Privacy
- All health data is processed on-device
- Use Health Connect for secure data storage
- Implement proper consent management

### 3. Performance
- Test on actual Galaxy Watch hardware
- Monitor memory usage and battery consumption
- Optimize TensorFlow Lite model inference

## Resources

- [Wear OS Developer Guide](https://developer.android.com/training/wearables)
- [Google Health Services](https://developer.android.com/guide/health-and-fitness/health-services)
- [Health Connect](https://developer.android.com/guide/health-and-fitness/health-connect)
- [Galaxy Watch Developer Resources](https://developer.samsung.com/galaxy-watch)