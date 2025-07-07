# Test script for FunBankApp API endpoints
Write-Host "Testing FunBankApp API endpoints..." -ForegroundColor Green

# Test 1: Health check
Write-Host "`n1. Testing health endpoint..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method GET
    Write-Host "Health check: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Register a new user
Write-Host "`n2. Testing user registration..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

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
    }
}

# Test 3: Login with the registered user
Write-Host "`n3. Testing user login..." -ForegroundColor Yellow
$loginBody = @{
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "Response: $($loginResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    
    # Extract token for protected endpoint test
    $loginToken = $loginResponse.data.token
    Write-Host "Login token: $($loginToken.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
    }
}

# Test 4: Test protected endpoint with token
Write-Host "`n4. Testing protected endpoint (user profile)..." -ForegroundColor Yellow
if ($loginToken) {
    try {
        $headers = @{
            "Authorization" = "Bearer $loginToken"
            "Content-Type" = "application/json"
        }
        
        $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
        Write-Host "Protected endpoint access successful!" -ForegroundColor Green
        Write-Host "User profile: $($profileResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } catch {
        Write-Host "Protected endpoint access failed: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response body: $responseBody" -ForegroundColor Red
        }
    }
} else {
    Write-Host "No token available for protected endpoint test" -ForegroundColor Yellow
}

# Test 5: Test token validation
Write-Host "`n5. Testing token validation..." -ForegroundColor Yellow
if ($loginToken) {
    try {
        $headers = @{
            "Authorization" = "Bearer $loginToken"
            "Content-Type" = "application/json"
        }
        
        $validateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/validate" -Method GET -Headers $headers
        Write-Host "Token validation successful!" -ForegroundColor Green
        Write-Host "Validation response: $($validateResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } catch {
        Write-Host "Token validation failed: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "No token available for validation test" -ForegroundColor Yellow
}

Write-Host "`nAPI testing completed!" -ForegroundColor Green 