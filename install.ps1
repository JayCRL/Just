# Just Language SDK Installer
# Version 1.0
# Usage: .\install.ps1

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Just Language SDK Installer v1.0" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检测安装位置
$DefaultPath = "C:\Just"
$InstallPath = Read-Host "Install location (default: $DefaultPath)"
if ([string]::IsNullOrWhiteSpace($InstallPath)) {
    $InstallPath = $DefaultPath
}

Write-Host ""
Write-Host "Installation directory: $InstallPath" -ForegroundColor Yellow
Write-Host ""

# 检查 Java
Write-Host "[1/5] Checking Java..." -ForegroundColor Yellow
try {
    $javaVersion = & java -version 2>&1 | Select-Object -First 1
    Write-Host "      Found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "      WARNING: Java not found" -ForegroundColor Red
    Write-Host "      Please install Java 11+ from:" -ForegroundColor Yellow
    Write-Host "      https://adoptium.net/" -ForegroundColor Yellow
    $continue = Read-Host "Continue anyway? (y/n)"
    if ($continue -ne "y") {
        exit 1
    }
}

# 检查 GCC
Write-Host "[2/5] Checking GCC..." -ForegroundColor Yellow
try {
    $gccVersion = & gcc --version 2>&1 | Select-Object -First 1
    Write-Host "      Found: $gccVersion" -ForegroundColor Green
} catch {
    Write-Host "      WARNING: GCC not found" -ForegroundColor Red
    Write-Host "      Please install MinGW-w64 from:" -ForegroundColor Yellow
    Write-Host "      https://winlibs.com/" -ForegroundColor Yellow
    $continue = Read-Host "Continue anyway? (y/n)"
    if ($continue -ne "y") {
        exit 1
    }
}

# 复制文件
Write-Host "[3/5] Copying files..." -ForegroundColor Yellow
if (Test-Path $InstallPath) {
    Write-Host "      Directory exists, removing..." -ForegroundColor Yellow
    Remove-Item $InstallPath -Recurse -Force
}

$SourcePath = Split-Path -Parent $MyInvocation.MyCommand.Path
Copy-Item $SourcePath $InstallPath -Recurse -Force
Write-Host "      Files copied to $InstallPath" -ForegroundColor Green

# 添加到 PATH
Write-Host "[4/5] Adding to PATH..." -ForegroundColor Yellow
$BinPath = "$InstallPath\bin"

# 获取当前用户 PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")

if ($currentPath -notlike "*$BinPath*") {
    $newPath = "$currentPath;$BinPath"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    Write-Host "      Added to User PATH" -ForegroundColor Green
    Write-Host "      Restart your terminal to use 'justc' and 'just' commands" -ForegroundColor Yellow
} else {
    Write-Host "      Already in PATH" -ForegroundColor Green
}

# 测试安装
Write-Host "[5/5] Testing installation..." -ForegroundColor Yellow

# 创建测试文件
$TestFile = "$InstallPath\test_install.just"
$TestCode = @"
class Main {
    void main() {
        println("Just SDK installed successfully!");
    }
}
"@
Set-Content -Path $TestFile -Value $TestCode

# 编译测试
Push-Location $InstallPath
$env:PATH = "$BinPath;$env:PATH"

& "$BinPath\justc.bat" "test_install.just" 2>&1 | Out-Null
if (Test-Path "test_install.exe") {
    Write-Host "      Compilation: OK" -ForegroundColor Green

    # 运行测试
    $output = & ".\test_install.exe" 2>&1
    if ($output -like "*successfully*") {
        Write-Host "      Execution: OK" -ForegroundColor Green
    }

    # 清理
    Remove-Item "test_install.exe" -ErrorAction SilentlyContinue
    Remove-Item "test_install.just" -ErrorAction SilentlyContinue
} else {
    Write-Host "      WARNING: Test compilation failed" -ForegroundColor Red
}

Pop-Location

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Installation Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Just SDK installed to: $InstallPath" -ForegroundColor Yellow
Write-Host ""
Write-Host "Quick Start:" -ForegroundColor Cyan
Write-Host "  1. Restart your terminal" -ForegroundColor White
Write-Host "  2. Create a file: hello.just" -ForegroundColor White
Write-Host "  3. Compile: justc hello.just" -ForegroundColor White
Write-Host "  4. Run: just hello" -ForegroundColor White
Write-Host ""
Write-Host "Documentation: $InstallPath\README.md" -ForegroundColor Yellow
Write-Host "Examples: $InstallPath\examples\" -ForegroundColor Yellow
Write-Host ""
Write-Host "GitHub: https://github.com/JayCRL/Just" -ForegroundColor Cyan
Write-Host ""
