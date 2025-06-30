@echo off
echo ========================================
echo FunBankApp Test Runner
echo ========================================

if "%1"=="detailed" (
    echo Running tests with detailed output...
    gradlew testDetailed
) else if "%1"=="unit" (
    echo Running unit tests only...
    gradlew unitTest
) else if "%1"=="integration" (
    echo Running integration tests only...
    gradlew integrationTest
) else if "%1"=="suite" (
    echo Running complete test suite...
    gradlew runTestSuite
) else (
    echo Running all tests...
    gradlew test
)

echo.
echo ========================================
echo Test execution completed!
echo ========================================
pause 