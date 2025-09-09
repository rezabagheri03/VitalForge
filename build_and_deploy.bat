@echo off
echo === VitalForge APK Builder ===

echo.
echo Cleaning project...
call gradlew.bat clean

echo.
echo Building Wear OS app...
call gradlew.bat :app:assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Wear OS app build failed!
    pause
    exit /b 1
)

echo.
echo Building Companion app...
call gradlew.bat :companion:assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Companion app build failed!
    pause
    exit /b 1
)

echo.
echo === Build Complete ===
echo.
echo APK files created:
echo Wear OS app: app\build\outputs\apk\debug\app-debug.apk
echo Companion app: companion\build\outputs\apk\debug\companion-debug.apk
echo.

echo Checking for connected devices...
adb devices

echo.
echo To install APKs:
echo 1. Wear OS: adb install app\build\outputs\apk\debug\app-debug.apk
echo 2. Companion: adb install companion\build\outputs\apk\debug\companion-debug.apk
echo.

pause