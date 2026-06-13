# Just Language SDK Uninstaller

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Just Language SDK Uninstaller" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$InstallPath = "C:\Just"

# 确认卸载
$confirm = Read-Host "Uninstall Just SDK from $InstallPath? (y/n)"
if ($confirm -ne "y") {
    Write-Host "Uninstall cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host ""

# 从 PATH 移除
Write-Host "[1/2] Removing from PATH..." -ForegroundColor Yellow
$BinPath = "$InstallPath\bin"
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")

if ($currentPath -like "*$BinPath*") {
    $newPath = $currentPath -replace [regex]::Escape(";$BinPath"), ""
    $newPath = $newPath -replace [regex]::Escape("$BinPath;"), ""
    $newPath = $newPath -replace [regex]::Escape("$BinPath"), ""
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    Write-Host "      Removed from PATH" -ForegroundColor Green
} else {
    Write-Host "      Not in PATH" -ForegroundColor Yellow
}

# 删除文件
Write-Host "[2/2] Removing files..." -ForegroundColor Yellow
if (Test-Path $InstallPath) {
    Remove-Item $InstallPath -Recurse -Force
    Write-Host "      Files removed" -ForegroundColor Green
} else {
    Write-Host "      Directory not found" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Uninstall Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Just SDK has been removed from your system." -ForegroundColor Yellow
Write-Host "Please restart your terminal." -ForegroundColor Yellow
Write-Host ""
