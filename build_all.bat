@echo off
REM -------------------------------
REM Environment check and build orchestrator (Windows)
REM -------------------------------

SETLOCAL ENABLEEXTENSIONS
SETLOCAL ENABLEDELAYEDEXPANSION

REM Exit on first error (simulated by checking ERRORLEVEL after commands)

REM Function to check if a command exists
CALL :check_command java || GOTO :fail
IF NOT DEFINED JAVA_HOME (
    where java >nul 2>&1
    IF ERRORLEVEL 1 (
        echo ❌ Neither 'java' command is available nor is JAVA_HOME set.
        EXIT /B 1
    )
)

CALL :check_command node || GOTO :fail
CALL :check_command npm || GOTO :fail
CALL :check_command npx || GOTO :fail
CALL :check_command go || GOTO :fail

echo ✅ Environment checks passed.
echo.

REM Ask user whether to build hardware component
SET /P build_hardware=🛠️  Do you want to build the hardware component? (y/n): 
SET "build_hardware=!build_hardware!"
CALL :to_lower build_hardware

REM Build order
SET DIRS=backend frontend

IF "!build_hardware!"=="y" (
    SET DIRS=%DIRS% hardware
)
IF "!build_hardware!"=="yes" (
    SET DIRS=%DIRS% hardware
)

REM Loop through components and build
FOR %%D IN (%DIRS%) DO (
    echo 📦 Building %%D...
    IF EXIST "%%D\build.bat" (
        PUSHD %%D
        CALL build.bat
        IF ERRORLEVEL 1 (
            echo ❌ Failed to build %%D.
            EXIT /B 1
        )
        POPD
        echo ✅ %%D built successfully.
    ) ELSE (
        echo ❌ %%D\build.bat not found or not executable.
        EXIT /B 1
    )
    echo.
)

echo 🎉 All components built successfully!
EXIT /B 0

REM -------------------------------
REM Functions
REM -------------------------------
:check_command
where %1 >nul 2>&1
IF ERRORLEVEL 1 (
    echo ❌ Required command '%1' is not installed or not in PATH.
    EXIT /B 1
)
EXIT /B 0

:to_lower
SETLOCAL ENABLEDELAYEDEXPANSION
SET "var=!%1!"
FOR %%A IN (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) DO (
    SET "var=!var:%%A=%%A!"
)
FOR %%A IN (a b c d e f g h i j k l m n o p q r s t u v w x y z) DO (
    SET "var=!var:%%A=%%A!"
)
ENDLOCAL & SET "%1=%var%"
EXIT /B 0

:fail
EXIT /B 1
