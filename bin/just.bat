@echo off
REM Just Runner - Simple as java
REM Usage: just <program>

if "%~1"=="" (
    echo Usage: just ^<program^>
    echo Example: just hello
    exit /b 1
)

set PROGRAM=%~1
set FULLPATH=%CD%\%PROGRAM%.exe

if not exist "%FULLPATH%" (
    echo Error: %PROGRAM%.exe not found
    echo Run: justc %PROGRAM%.just first
    exit /b 1
)

"%FULLPATH%"
