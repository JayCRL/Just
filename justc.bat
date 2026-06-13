@echo off
REM Just 语言编译脚本 (Windows)

setlocal EnableDelayedExpansion

if "%~1"=="" (
    echo 用法: justc.bat ^<source.just^>
    echo 示例: justc.bat examples\hello.just
    exit /b 1
)

set SOURCE=%~1
set BASENAME=%~n1
set COMPILER_DIR=%~dp0compiler
set RUNTIME_DIR=%~dp0runtime

echo ====================================
echo Just 编译器 v0.1
echo ====================================
echo.

REM 检查源文件是否存在
if not exist "%SOURCE%" (
    echo 错误: 文件 "%SOURCE%" 不存在
    exit /b 1
)

REM 第一步：检查 Java
echo [1/4] 检查 Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到 Java。请安装 JDK 8 或更高版本。
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    exit /b 1
)
echo ✓ Java 已安装
echo.

REM 第二步：编译 Just 编译器（如果需要）
if not exist "%COMPILER_DIR%\bin\just\JustCompiler.class" (
    echo [2/4] 编译 Just 编译器...
    if not exist "%COMPILER_DIR%\bin" mkdir "%COMPILER_DIR%\bin"
    javac -d "%COMPILER_DIR%\bin" -encoding UTF-8 "%COMPILER_DIR%\src\just\*.java" "%COMPILER_DIR%\src\just\lexer\*.java" "%COMPILER_DIR%\src\just\parser\*.java" "%COMPILER_DIR%\src\just\ast\*.java" "%COMPILER_DIR%\src\just\semantic\*.java" "%COMPILER_DIR%\src\just\codegen\*.java"
    if errorlevel 1 (
        echo 错误: Just 编译器编译失败
        exit /b 1
    )
    echo ✓ Just 编译器编译成功
) else (
    echo [2/4] Just 编译器已存在，跳过编译
)
echo.

REM 第三步：运行 Just 编译器
echo [3/4] 编译 Just 源代码...
java -cp "%COMPILER_DIR%\bin" just.JustCompiler "%SOURCE%"
if errorlevel 1 (
    echo 错误: Just 源代码编译失败
    exit /b 1
)
echo.

REM 第四步：使用 GCC 编译生成的 C 代码
echo [4/4] 编译 C 代码...
gcc -o "%BASENAME%.exe" "%BASENAME%.c" "%RUNTIME_DIR%\runtime.c" -I"%RUNTIME_DIR%" 2>nul
if errorlevel 1 (
    echo 警告: GCC 编译失败（可能未安装 GCC）
    echo 生成的 C 代码已保存为 %BASENAME%.c 和 %BASENAME%.h
    echo.
    echo 安装 GCC 后，可以手动编译：
    echo   gcc -o %BASENAME%.exe %BASENAME%.c runtime\runtime.c -Iruntime
    exit /b 0
)
echo ✓ 编译成功！
echo.

echo ====================================
echo 编译完成！
echo ====================================
echo 生成的文件:
echo   - %BASENAME%.h (头文件)
echo   - %BASENAME%.c (C 源代码)
echo   - %BASENAME%.exe (可执行文件)
echo.
echo 运行程序:
echo   %BASENAME%.exe
echo.

endlocal
