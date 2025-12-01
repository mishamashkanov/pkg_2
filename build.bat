@echo off
echo Building Graphics File Analyzer...
echo.

REM Create directories if they don't exist
if not exist build mkdir build
if not exist dist mkdir dist

REM Compile all Java files
echo Compiling source code...
javac -encoding UTF-8 -d build ^
    src/Main.java ^
    src/ImageInfoApp.java ^
    src/ControlPanel.java ^
    src/ImageInfoPanel.java ^
    src/ImageInfoProcessor.java ^
    src/ImageInfo.java ^
    src/StatusPanel.java

if errorlevel 1 (
    echo Compilation error!
    pause
    exit /b 1
)

echo Build completed successfully!
pause