@echo off
REM Just 语言一键编译脚本 (Windows)
REM 用法: justc <source.just> [options]

setlocal EnableDelayedExpansion

REM 配置路径
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot"
set "JUST_HOME=%~dp0"
set "COMPILER_DIR=%JUST_HOME%compiler"
set "RUNTIME_DIR=%JUST_HOME%runtime"
set "GCC_PATH=C:\Users\25566\mingw64\mingw64\bin"

REM 默认值
set "OUTPUT="
set "RUN=0"
set "VERBOSE=0"
set "KEEP_C=0"

REM 检查参数
if "%~1"=="" (
    echo Just Compiler - 一键编译工具
    echo.
    echo 用法: justc ^<source.just^> [options]
    echo.
    echo 选项:
    echo   -o ^<name^>    指定输出文件名 ^(不含 .exe^)
    echo   --run        编译后立即运行
    echo   --verbose    显示详细编译信息
    echo   --keep-c     保留生成的 C 代码
    echo.
    echo 示例:
    echo   justc hello.just              编译为 hello.exe
    echo   justc hello.just -o myapp     编译为 myapp.exe
    echo   justc hello.just --run        编译并运行
    exit /b 1
)

set "SOURCE=%~1"
set "BASENAME=%~n1"
set "SOURCEDIR=%~dp1"

REM 解析选项
shift
:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="-o" (
    set "OUTPUT=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--run" (
    set "RUN=1"
    shift
    goto parse_args
)
if "%~1"=="--verbose" (
    set "VERBOSE=1"
    shift
    goto parse_args
)
if "%~1"=="--keep-c" (
    set "KEEP_C=1"
    shift
    goto parse_args
)
shift
goto parse_args
:end_parse

REM 设置输出名称
if "%OUTPUT%"=="" set "OUTPUT=%BASENAME%"

REM 检查源文件
if not exist "%SOURCE%" (
    echo 错误: 找不到源文件 "%SOURCE%"
    exit /b 1
)

echo ========================================
echo Just Compiler - 一键编译
echo ========================================
echo 源文件: %SOURCE%
echo 输出:   %OUTPUT%.exe
echo ========================================
echo.

REM 步骤 1: Just → C
echo [1/3] 编译 Just 代码到 C...
if %VERBOSE%==1 (
    "%JAVA_HOME%\bin\java.exe" -cp "%COMPILER_DIR%\bin" just.JustCompiler "%SOURCE%"
) else (
    "%JAVA_HOME%\bin\java.exe" -cp "%COMPILER_DIR%\bin" just.JustCompiler "%SOURCE%" >nul 2>&1
)

if errorlevel 1 (
    echo 错误: Just 编译失败
    "%JAVA_HOME%\bin\java.exe" -cp "%COMPILER_DIR%\bin" just.JustCompiler "%SOURCE%"
    exit /b 1
)
echo       ✓ C 代码生成成功

REM 步骤 2: 准备编译
set "C_FILE=%SOURCEDIR%%BASENAME%.c"
set "H_FILE=%SOURCEDIR%%BASENAME%.h"
if exist "%H_FILE%" (
    copy /Y "%H_FILE%" "%SOURCEDIR%just_generated.h" >nul 2>&1
)

REM 步骤 3: C → 可执行文件
echo [2/3] 使用 GCC 编译 C 代码...
if %VERBOSE%==1 (
    "%GCC_PATH%\gcc.exe" -o "%OUTPUT%.exe" "%C_FILE%" "%RUNTIME_DIR%\runtime.c" -I"%SOURCEDIR%" -I"%RUNTIME_DIR%" -O2
) else (
    "%GCC_PATH%\gcc.exe" -o "%OUTPUT%.exe" "%C_FILE%" "%RUNTIME_DIR%\runtime.c" -I"%SOURCEDIR%" -I"%RUNTIME_DIR%" -O2 2>nul
)

if errorlevel 1 (
    echo 错误: GCC 编译失败
    "%GCC_PATH%\gcc.exe" -o "%OUTPUT%.exe" "%C_FILE%" "%RUNTIME_DIR%\runtime.c" -I"%SOURCEDIR%" -I"%RUNTIME_DIR%" -O2
    exit /b 1
)
echo       ✓ 可执行文件生成成功

REM 清理 C 文件（如果不需要保留）
if %KEEP_C%==0 (
    if exist "%C_FILE%" del "%C_FILE%" 2>nul
    if exist "%H_FILE%" del "%H_FILE%" 2>nul
    if exist "%SOURCEDIR%just_generated.h" del "%SOURCEDIR%just_generated.h" 2>nul
)

echo [3/3] 完成！
echo.
echo ========================================
echo ✓ 编译成功: %OUTPUT%.exe
echo ========================================
echo.

REM 运行程序
if %RUN%==1 (
    echo 正在运行程序...
    echo ----------------------------------------
    "%OUTPUT%.exe"
    echo ----------------------------------------
    echo.
)

endlocal

