@echo off
echo Starting Graphics File Analyzer...
echo.

REM Check if project is compiled
if not exist build (
    echo Project not compiled. Run build.bat first.
    pause
    exit /b 1
)

REM Run the application
java -cp build Main

pause