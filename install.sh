#!/bin/bash
# Just Language SDK Installer for Linux
# Version 1.0

set -e

echo "========================================"
echo "Just Language SDK Installer v1.0"
echo "========================================"
echo ""

# 检查是否为 root
if [ "$EUID" -ne 0 ]; then
    echo "Please run as root (use sudo)"
    exit 1
fi

# 安装目录
INSTALL_DIR="/opt/just"
BIN_DIR="/usr/local/bin"

echo "Installation directory: $INSTALL_DIR"
echo ""

# 检查 Java
echo "[1/5] Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "      Found: $JAVA_VERSION"
else
    echo "      Installing OpenJDK 11..."
    apt-get update -qq
    apt-get install -y openjdk-11-jdk-headless
    echo "      OpenJDK 11 installed"
fi

# 检查 GCC
echo "[2/5] Checking GCC..."
if command -v gcc &> /dev/null; then
    GCC_VERSION=$(gcc --version | head -n 1)
    echo "      Found: $GCC_VERSION"
else
    echo "      Installing GCC..."
    apt-get install -y build-essential
    echo "      GCC installed"
fi

# 下载 Just SDK
echo "[3/5] Downloading Just SDK..."
if [ -d "$INSTALL_DIR" ]; then
    echo "      Removing old installation..."
    rm -rf "$INSTALL_DIR"
fi

# 从 GitHub 下载
if command -v git &> /dev/null; then
    git clone https://github.com/JayCRL/Just.git "$INSTALL_DIR" --quiet
else
    echo "      Installing git..."
    apt-get install -y git
    git clone https://github.com/JayCRL/Just.git "$INSTALL_DIR" --quiet
fi
echo "      Downloaded to $INSTALL_DIR"

# 创建可执行命令
echo "[4/5] Creating commands..."

# justc 命令
cat > "$BIN_DIR/justc" << 'EOF'
#!/bin/bash
JUST_HOME="/opt/just"
JAVA_CMD="java"

if [ -z "$1" ]; then
    echo "Usage: justc <file.just>"
    exit 1
fi

SOURCE="$1"
BASENAME="${SOURCE%.just}"

echo "Compiling $SOURCE..."

# Compile Just to C
$JAVA_CMD -cp "$JUST_HOME/compiler/bin" just.JustCompiler "$SOURCE" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Compilation failed"
    $JAVA_CMD -cp "$JUST_HOME/compiler/bin" just.JustCompiler "$SOURCE"
    exit 1
fi

# Compile C to executable
cp "$BASENAME.h" "just_generated.h" 2>/dev/null
gcc -o "$BASENAME" "$BASENAME.c" "$JUST_HOME/runtime/runtime.c" -I. -I"$JUST_HOME/runtime" -O2 > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Linking failed"
    exit 1
fi

# Cleanup
rm -f "$BASENAME.c" "$BASENAME.h" "just_generated.h"

echo "Success: $BASENAME"
EOF

chmod +x "$BIN_DIR/justc"

# just 命令
cat > "$BIN_DIR/just" << 'EOF'
#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: just <program>"
    exit 1
fi

PROGRAM="$1"

if [ ! -f "$PROGRAM" ]; then
    echo "Error: $PROGRAM not found"
    echo "Run: justc $PROGRAM.just first"
    exit 1
fi

./"$PROGRAM"
EOF

chmod +x "$BIN_DIR/just"

echo "      Commands created: justc, just"

# 测试安装
echo "[5/5] Testing installation..."

cd /tmp
cat > test_install.just << 'EOF'
class Main {
    void main() {
        println("Just SDK installed successfully!");
    }
}
EOF

justc test_install.just > /dev/null 2>&1
if [ -f "test_install" ]; then
    echo "      Compilation: OK"
    OUTPUT=$(./test_install 2>&1)
    if [[ "$OUTPUT" == *"successfully"* ]]; then
        echo "      Execution: OK"
    fi
    rm -f test_install test_install.just
else
    echo "      WARNING: Test compilation failed"
fi

echo ""
echo "========================================"
echo "Installation Complete!"
echo "========================================"
echo ""
echo "Just SDK installed to: $INSTALL_DIR"
echo ""
echo "Quick Start:"
echo "  1. Create a file: hello.just"
echo "  2. Compile: justc hello.just"
echo "  3. Run: just hello"
echo ""
echo "Documentation: $INSTALL_DIR/README.md"
echo "Examples: $INSTALL_DIR/examples/"
echo ""
echo "GitHub: https://github.com/JayCRL/Just"
echo ""
