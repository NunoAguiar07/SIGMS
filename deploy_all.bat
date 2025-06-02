@echo off
REM -------------------------------------
REM Docker setup and build script (Windows)
REM -------------------------------------

SETLOCAL ENABLEEXTENSIONS
SETLOCAL ENABLEDELAYEDEXPANSION

REM Exit on first error simulation
REM (manually checking %ERRORLEVEL% after each step)

REM Check if Docker is installed
where docker >nul 2>&1
IF ERRORLEVEL 1 (
    echo Docker is not installed or not in your PATH.
    EXIT /B 1
)

REM Check if Docker Compose is available (plugin or legacy)
docker compose version >nul 2>&1
IF ERRORLEVEL 1 (
    echo Docker Compose is not available. Please install it.
    EXIT /B 1
)

REM Get the directory of this script
SET "SCRIPT_DIR=%~dp0"
SET "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"  REM Remove trailing backslash

REM Run the build_all.bat script
echo Running build_all.bat...
CALL "%SCRIPT_DIR%\build_all.bat"
IF ERRORLEVEL 1 EXIT /B 1

REM Change to the docker directory
cd /d "%SCRIPT_DIR%\docker"

REM Run Docker Compose build
echo Running docker compose build...
docker compose build
IF ERRORLEVEL 1 EXIT /B 1

REM Run Docker Compose up
echo Running docker compose up...
docker compose up -d
IF ERRORLEVEL 1 EXIT /B 1

echo ✅ Docker services are up and running!
