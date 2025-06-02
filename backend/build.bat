@echo off
REM ----------------------------------
REM Gradle project build script (Windows)
REM Usage: build.bat [clean|test|jar|build]
REM ----------------------------------

SETLOCAL ENABLEEXTENSIONS
SETLOCAL ENABLEDELAYEDEXPANSION

REM Get current directory (equivalent to PROJECT_DIR)
SET "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

echo Building Gradle project in: %PROJECT_DIR%

REM Check if gradlew exists
IF EXIST "gradlew" (
    SET "GRADLE_CMD=gradlew"
) ELSE (
    where gradle >nul 2>&1
    IF %ERRORLEVEL% EQU 0 (
        SET "GRADLE_CMD=gradle"
    ) ELSE (
        echo Error: Neither gradlew nor gradle command found!
        EXIT /B 1
    )
)

REM Handle arguments
SET "ARG=%1"
IF "%ARG%"=="" SET "ARG=build"

IF /I "%ARG%"=="clean" (
    echo Cleaning project...
    %GRADLE_CMD% clean
    IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
    echo Clean completed!
) ELSE IF /I "%ARG%"=="test" (
    echo Running tests...
    %GRADLE_CMD% test
    IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
    echo Tests completed!
) ELSE IF /I "%ARG%"=="jar" (
    echo Creating JAR...
    %GRADLE_CMD% jar
    IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
    echo JAR created successfully!
) ELSE IF /I "%ARG%"=="build" (
    echo Compiling project...
    %GRADLE_CMD% build
    IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
    echo Build completed successfully!
) ELSE (
    echo Usage: build.bat [clean^|test^|jar^|build]
    echo   clean - Clean the project
    echo   test  - Run tests
    echo   jar   - Create JAR file
    echo   build - Compile project (default)
    EXIT /B 1
)

ENDLOCAL
