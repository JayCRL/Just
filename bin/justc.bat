@echo off
setlocal
set JUST_HOME=C:\Users\25566\just-lang
set JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot
set GCC_HOME=C:\Users\25566\mingw64\mingw64

if "%~1"=="" (
    echo Usage: justc ^<file.just^>
    exit /b 1
)

set SOURCE=%~1
set BASENAME=%~n1

echo Compiling %SOURCE%...

REM Step 1: Just to C
"%JAVA_HOME%\bin\java.exe" -cp "%JUST_HOME%\compiler\bin" just.JustCompiler "%SOURCE%" >nul 2>&1
if errorlevel 1 (
    "%JAVA_HOME%\bin\java.exe" -cp "%JUST_HOME%\compiler\bin" just.JustCompiler "%SOURCE%"
    exit /b 1
)

REM Step 2: C to EXE
copy /Y "%BASENAME%.h" "just_generated.h" >nul 2>&1
"%GCC_HOME%\bin\gcc.exe" -o "%BASENAME%.exe" "%BASENAME%.c" "%JUST_HOME%\runtime\runtime.c" -I. -I"%JUST_HOME%\runtime" -O2 >nul 2>&1
if errorlevel 1 (
    echo Linking failed
    exit /b 1
)

REM Cleanup
del "%BASENAME%.c" "%BASENAME%.h" "just_generated.h" 2>nul

echo Success: %BASENAME%.exe

endlocal
