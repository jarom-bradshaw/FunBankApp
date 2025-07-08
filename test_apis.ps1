# PowerShell API Test Script for FunBankApp

$baseUrl = "http://localhost:8080"
$email = "testuser@gmail.com"
$password = "password123"

Write-Host "Logging in to get JWT token..."
$loginBody = @{ email = $email; password = $password } | ConvertTo-Json
$response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody -ErrorAction Stop

$jwt = $response.data.token
if (-not $jwt) {
    Write-Host "Login failed! Response:"
    $response | ConvertTo-Json
    exit 1
}
Write-Host "Login successful. JWT acquired."
$headers = @{ Authorization = "Bearer $jwt" }

function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null
    )
    $url = "$baseUrl$Endpoint"
    Write-Host "\nTesting $Method $Endpoint"
    try {
        if ($Method -eq "GET") {
            $result = Invoke-RestMethod -Uri $url -Headers $headers -Method Get -ErrorAction Stop
            Write-Host "Status: 200 (OK)"
            $result | ConvertTo-Json -Depth 5
        } elseif ($Method -eq "POST") {
            $jsonBody = $Body | ConvertTo-Json
            $result = Invoke-RestMethod -Uri $url -Headers $headers -Method Post -ContentType "application/json" -Body $jsonBody -ErrorAction Stop
            Write-Host "Status: 200 (OK)"
            $result | ConvertTo-Json -Depth 5
        } elseif ($Method -eq "PUT") {
            $jsonBody = $Body | ConvertTo-Json
            $result = Invoke-RestMethod -Uri $url -Headers $headers -Method Put -ContentType "application/json" -Body $jsonBody -ErrorAction Stop
            Write-Host "Status: 200 (OK)"
            $result | ConvertTo-Json -Depth 5
        } elseif ($Method -eq "DELETE") {
            $result = Invoke-RestMethod -Uri $url -Headers $headers -Method Delete -ErrorAction Stop
            Write-Host "Status: 200 (OK)"
            $result | ConvertTo-Json -Depth 5
        }
    } catch {
        Write-Host "Status: $($_.Exception.Response.StatusCode.value__)"
        $errResp = $_.ErrorDetails.Message | ConvertFrom-Json
        $errResp | ConvertTo-Json -Depth 5
    }
}

# Test endpoints (add more as needed)
Test-Endpoint GET "/api/debt-strategies/compare"
Test-Endpoint GET "/api/debt-strategies"
Test-Endpoint GET "/api/debt-strategies/active"
Test-Endpoint GET "/api/debt-strategies/generate/snowball"
Test-Endpoint GET "/api/debt-strategies/generate/avalanche"

# Example POST endpoints (uncomment and adjust IDs as needed)
# Test-Endpoint POST "/api/accounts/deposit" @{ accountId = 1; amount = 100; description = "Test deposit" }
# Test-Endpoint POST "/api/accounts/withdraw" @{ accountId = 1; amount = 50; description = "Test withdraw" }
# Test-Endpoint POST "/api/accounts/transfer" @{ fromAccountId = 1; toAccountId = 2; amount = 25; description = "Test transfer" }

Write-Host "\nAll tests complete." 