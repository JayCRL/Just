@echo off
REM Just 语言一键编译脚本
setlocal

if "%~1"=="" (
    echo 用法: justc ^<source.just^> [--run] [--keep-c]
    echo.
    echo 示例:
    echo   justc hello.just          编译
    echo   justc hello.just --run    编译并运行
    echo   justc hello.just --keep-c 保留C代码
    exit /b 1
)

set SOURCE=%~1
set BASENAME=%~n1
set RUN=0
set KEEP_C=0

if "%~2"=="--run" set RUN=1
if "%~3"=="--run" set RUN=1
if "%~2"=="--keep-c" set KEEP_C=1
if "%~3"=="--keep-c" set KEEP_C=1

if not exist "%SOURCE%" (
    echo 错误: 找不到文件 %SOURCE%
    exit /b 1
)

echo ========================================
echo Just 一键编译
echo ========================================
echo 源文件: %SOURCE%
echo 输出:   %BASENAME%.exe
echo ========================================
echo.

REM 步骤 1: Just → C
echo [1/3] 编译 Just 代码...
set JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot
"%JAVA_HOME%\bin\java.exe" -cp "%~dp0compiler\bin" just.JustCompiler "%SOURCE%" >nul 2>&1
if errorlevel 1 (
    echo 错误: Just 编译失败
    "%JAVA_HOME%\bin\java.exe" -cp "%~dp0compiler\bin" just.JustCompiler "%SOURCE%"
    exit /b 1
)
echo       ✓ C 代码已生成

REM 步骤 2: 复制头文件
set SRCDIR=%~dp1
if exist "%SRCDIR%%BASENAME%.h" (
    copy /Y "%SRCDIR%%BASENAME%.h" "%SRCDIR%just_generated.h" >nul 2>&1
)

REM 步骤 3: C → EXE
echo [2/3] 编译 C 代码...
set GCC_PATH=C:\Users\25566\mingw64\mingw64\bin
"%GCC_PATH%\gcc.exe" -o "%BASENAME%.exe" "%SRCDIR%%BASENAME%.c" "%~dp0runtime\runtime.c" -I"%SRCDIR%" -I"%~dp0runtime" -O2 2>nul
if errorlevel 1 (
    echo 错误: GCC 编译失败
    "%GCC_PATH%\gcc.exe" -o "%BASENAME%.exe" "%SRCDIR%%BASENAME%.c" "%~dp0runtime\runtime.c" -I"%SRCDIR%" -I"%~dp0runtime" -O2
    exit /b 1
)
echo       ✓ 可执行文件已生成

REM 清理
if %KEEP_C%==0 (
    del "%SRCDIR%%BASENAME%.c" 2>nul
    del "%SRCDIR%%BASENAME%.h" 2>nul
    del "%SRCDIR%just_generated.h" 2>nul
)

echo [3/3] 完成！
echo.
echo ========================================
echo ✓ 编译成功: %BASENAME%.exe
echo ========================================
echo.

REM 运行
if %RUN%==1 (
    echo 运行程序:
    echo ----------------------------------------
    "%BASENAME%.exe"
    echo ----------------------------------------
)

endlocal
