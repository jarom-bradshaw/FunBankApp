# Detailed test script for FunBankApp API endpoints
Write-Host "Testing FunBankApp API endpoints with detailed error reporting..." -ForegroundColor Green

# Test 1: Health check
Write-Host "`n1. Testing health endpoint..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method GET
    Write-Host "Health check: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Register a new user with detailed error reporting
Write-Host "`n2. Testing user registration..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

Write-Host "Sending registration request with body: $registerBody" -ForegroundColor Cyan

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
    Write-Host "Registration successful!" -ForegroundColor Green
    Write-Host "Response: $($registerResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    
    # Extract token for login test
    $token = $registerResponse.data.token
    Write-Host "Token received: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
        
        # Try to parse as JSON
        try {
            $errorJson = $responseBody | ConvertFrom-Json
            Write-Host "Error details: $($errorJson | ConvertTo-Json)" -ForegroundColor Red
        } catch {
            Write-Host "Could not parse error response as JSON" -ForegroundColor Red
        }
    }
}

# Test 3: Try registration with minimal data
Write-Host "`n3. Testing registration with minimal data..." -ForegroundColor Yellow
$minimalBody = @{
    username = "testuser2"
    email = "test2@example.com"
    password = "password123"
} | ConvertTo-Json

Write-Host "Sending minimal registration request with body: $minimalBody" -ForegroundColor Cyan

try {
    $minimalResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $minimalBody
    Write-Host "Minimal registration successful!" -ForegroundColor Green
    Write-Host "Response: $($minimalResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
} catch {
    Write-Host "Minimal registration failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`nDetailed API testing completed!" -ForegroundColor Green 