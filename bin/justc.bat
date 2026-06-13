@echo off
setlocal
set "JUST_HOME=%~dp0.."
set "JAVA_EXE=%JUST_HOME%\tools\jre\bin\java.exe"
set "GCC_EXE=%JUST_HOME%\tools\mingw64\bin\gcc.exe"

if not exist "%JAVA_EXE%" (
    set "JAVA_EXE=java"
)

if not exist "%GCC_EXE%" (
    set "GCC_EXE=gcc"
)

if "%~1"=="" (
    echo Usage: justc ^<file.just^>
    exit /b 1
)

set SOURCE=%~1
set BASENAME=%~n1

echo Compiling %SOURCE%...

REM Step 1: Just to C
"%JAVA_EXE%" -cp "%JUST_HOME%\compiler\bin" just.JustCompiler "%SOURCE%" >nul 2>&1
if errorlevel 1 (
    "%JAVA_EXE%" -cp "%JUST_HOME%\compiler\bin" just.JustCompiler "%SOURCE%"
    exit /b 1
)

REM Step 2: C to EXE
copy /Y "%BASENAME%.h" "just_generated.h" >nul 2>&1
"%GCC_EXE%" -o "%BASENAME%.exe" "%BASENAME%.c" "%JUST_HOME%\runtime\runtime.c" -I. -I"%JUST_HOME%\runtime" -O2 >nul 2>&1
if errorlevel 1 (
    echo Linking failed
    exit /b 1
)

REM Cleanup
del "%BASENAME%.c" "%BASENAME%.h" "just_generated.h" 2>nul

echo Success: %BASENAME%.exe

endlocal
