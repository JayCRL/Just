#!/usr/bin/env pwsh
# Just Language - One-Click Compiler
# Usage: .\just.ps1 <source.just>

param(
    [Parameter(Mandatory=$true)]
    [string]$Source
)

$ErrorActionPreference = "Stop"
$BaseName = [System.IO.Path]::GetFileNameWithoutExtension($Source)
$JustHome = Split-Path -Parent $MyInvocation.MyCommand.Path

if (-not (Test-Path $Source)) {
    Write-Host "Error: File not found: $Source" -ForegroundColor Red
    exit 1
}

Write-Host "Just Compiler" -ForegroundColor Cyan
Write-Host "Source: $Source -> $BaseName.exe" -ForegroundColor Gray
Write-Host ""

# Step 1: Just -> C
& "$JustHome\C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot\bin\java.exe" -cp "$JustHome\compiler\bin" just.JustCompiler $Source 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed" -ForegroundColor Red
    exit 1
}

# Step 2: C -> EXE
Copy-Item "$BaseName.h" "just_generated.h" -Force -ErrorAction SilentlyContinue
& "C:\Users\25566\mingw64\mingw64\bin\gcc.exe" -o "$BaseName.exe" "$BaseName.c" "$JustHome\runtime\runtime.c" -I. -I"$JustHome\runtime" -O2 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Linking failed" -ForegroundColor Red
    exit 1
}

# Cleanup
Remove-Item "$BaseName.c" -ErrorAction SilentlyContinue
Remove-Item "$BaseName.h" -ErrorAction SilentlyContinue
Remove-Item "just_generated.h" -ErrorAction SilentlyContinue

Write-Host "Success: $BaseName.exe" -ForegroundColor Green
