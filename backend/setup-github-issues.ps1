# Setup script for creating GitHub issues
Write-Host "GitHub Issue Creation Setup" -ForegroundColor Green
Write-Host "==========================" -ForegroundColor Green
Write-Host ""

Write-Host "To create GitHub issues, you need a GitHub Personal Access Token." -ForegroundColor Yellow
Write-Host ""

Write-Host "Steps to get a GitHub token:" -ForegroundColor Cyan
Write-Host "1. Go to https://github.com/settings/tokens" -ForegroundColor White
Write-Host "2. Click 'Generate new token (classic)'" -ForegroundColor White
Write-Host "3. Give it a name like 'FunBankApp Issue Creation'" -ForegroundColor White
Write-Host "4. Select these scopes:" -ForegroundColor White
Write-Host "   - repo (Full control of private repositories)" -ForegroundColor White
Write-Host "   - write:packages (Upload packages to GitHub Package Registry)" -ForegroundColor White
Write-Host "5. Click 'Generate token'" -ForegroundColor White
Write-Host "6. Copy the token (you won't see it again!)" -ForegroundColor White
Write-Host ""

$token = Read-Host "Enter your GitHub token" -AsSecureString
$tokenPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($token))

if ($tokenPlain) {
    Write-Host ""
    Write-Host "Token received! Creating GitHub issues..." -ForegroundColor Green
    Write-Host ""
    
    # Run the issue creation script
    & .\create-github-issues.ps1 -GitHubToken $tokenPlain
} else {
    Write-Host "No token provided. Exiting." -ForegroundColor Red
} 