@echo off
echo Running Unbroken Chain App tests...
echo.

echo 1. Running unit tests...
call gradlew test
if %ERRORLEVEL% NEQ 0 (
    echo Unit tests failed!
    pause
    exit /b 1
)

echo.
echo 2. Running instrumentation tests...
call gradlew connectedAndroidTest
if %ERRORLEVEL% NEQ 0 (
    echo Instrumentation tests failed!
    pause
    exit /b 1
)

echo.
echo All tests completed!
echo Test results generated in app/build/reports/ directory
pause 