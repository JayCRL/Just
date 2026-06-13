# Just 语言一键编译脚本 (PowerShell)
# 用法: .\justc.ps1 <source.just> [options]

param(
    [Parameter(Mandatory=$true, Position=0)]
    [string]$Source,

    [switch]$Run,
    [switch]$KeepC,
    [switch]$Verbose
)

# 配置
$JAVA_HOME = "C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot"
$JUST_HOME = Split-Path -Parent $MyInvocation.MyCommand.Path
$GCC_PATH = "C:\Users\25566\mingw64\mingw64\bin"

# 检查源文件
if (-not (Test-Path $Source)) {
    Write-Host "错误: 找不到源文件 $Source" -ForegroundColor Red
    exit 1
}

$SourceFile = Get-Item $Source
$BaseName = $SourceFile.BaseName
$SourceDir = $SourceFile.DirectoryName

Write-Host "========================================"  -ForegroundColor Cyan
Write-Host "Just Compiler - 一键编译" -ForegroundColor Cyan
Write-Host "========================================"  -ForegroundColor Cyan
Write-Host "源文件: $Source"
Write-Host "输出:   $BaseName.exe"
Write-Host "========================================"  -ForegroundColor Cyan
Write-Host ""

# 步骤 1: Just → C
Write-Host "[1/3] 编译 Just 代码..." -ForegroundColor Yellow
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

if ($Verbose) {
    java -cp "$JUST_HOME\compiler\bin" just.JustCompiler $Source
} else {
    java -cp "$JUST_HOME\compiler\bin" just.JustCompiler $Source 2>&1 | Out-Null
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "错误: Just 编译失败" -ForegroundColor Red
    java -cp "$JUST_HOME\compiler\bin" just.JustCompiler $Source
    exit 1
}
Write-Host "      ✓ C 代码已生成" -ForegroundColor Green

# 步骤 2: 复制头文件
$HFile = "$SourceDir\$BaseName.h"
if (Test-Path $HFile) {
    Copy-Item $HFile "$SourceDir\just_generated.h" -Force
}

# 步骤 3: C → EXE
Write-Host "[2/3] 编译 C 代码..." -ForegroundColor Yellow
$env:PATH = "$GCC_PATH;$env:PATH"

$CFile = "$SourceDir\$BaseName.c"
$ExeFile = "$BaseName.exe"

if ($Verbose) {
    & "$GCC_PATH\gcc.exe" -o $ExeFile $CFile "$JUST_HOME\runtime\runtime.c" -I"$SourceDir" -I"$JUST_HOME\runtime" -O2
} else {
    & "$GCC_PATH\gcc.exe" -o $ExeFile $CFile "$JUST_HOME\runtime\runtime.c" -I"$SourceDir" -I"$JUST_HOME\runtime" -O2 2>&1 | Out-Null
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "错误: GCC 编译失败" -ForegroundColor Red
    & "$GCC_PATH\gcc.exe" -o $ExeFile $CFile "$JUST_HOME\runtime\runtime.c" -I"$SourceDir" -I"$JUST_HOME\runtime" -O2
    exit 1
}
Write-Host "      ✓ 可执行文件已生成" -ForegroundColor Green

# 清理
if (-not $KeepC) {
    Remove-Item $CFile -ErrorAction SilentlyContinue
    Remove-Item $HFile -ErrorAction SilentlyContinue
    Remove-Item "$SourceDir\just_generated.h" -ErrorAction SilentlyContinue
}

Write-Host "[3/3] 完成！" -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✓ 编译成功: $ExeFile" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 运行
if ($Run) {
    Write-Host "运行程序:" -ForegroundColor Yellow
    Write-Host "----------------------------------------"
    & ".\$ExeFile"
    Write-Host "----------------------------------------"
}
