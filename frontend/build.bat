@echo off
REM -----------------------------
REM Export Web Build via Expo
REM -----------------------------

REM Exit immediately if a command fails
SETLOCAL ENABLEEXTENSIONS
SETLOCAL ENABLEDELAYEDEXPANSION

echo 🌐 Starting Expo Web export...

REM Run Expo export for web
npx expo export --platform web
IF %ERRORLEVEL% EQU 0 (
    echo ✅ Web export completed successfully!
) ELSE (
    echo ❌ Export failed. Please check the logs above.
    exit /b 1
)
