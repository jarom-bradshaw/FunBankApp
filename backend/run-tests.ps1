# FunBankApp Test Runner PowerShell Script
# Usage: .\run-tests.ps1 [detailed|unit|integration|suite]

param(
    [Parameter(Position=0)]
    [ValidateSet("detailed", "unit", "integration", "suite")]
    [string]$TestType = "all"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "FunBankApp Test Runner" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

try {
    switch ($TestType) {
        "detailed" {
            Write-Host "Running tests with detailed output..." -ForegroundColor Yellow
            .\gradlew testDetailed
        }
        "unit" {
            Write-Host "Running unit tests only..." -ForegroundColor Yellow
            .\gradlew unitTest
        }
        "integration" {
            Write-Host "Running integration tests only..." -ForegroundColor Yellow
            .\gradlew integrationTest
        }
        "suite" {
            Write-Host "Running complete test suite..." -ForegroundColor Yellow
            .\gradlew runTestSuite
        }
        default {
            Write-Host "Running all tests..." -ForegroundColor Yellow
            .\gradlew test
        }
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n========================================" -ForegroundColor Green
        Write-Host "All tests passed successfully!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
    } else {
        Write-Host "`n========================================" -ForegroundColor Red
        Write-Host "Some tests failed!" -ForegroundColor Red
        Write-Host "========================================" -ForegroundColor Red
    }
} catch {
    Write-Host "Error running tests: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`nPress any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 