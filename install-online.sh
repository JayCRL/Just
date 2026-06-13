#!/bin/bash
# Just Language - One-Line Installer
# Usage: curl -fsSL http://8.162.0.88/just/install.sh | sudo bash

set -e

echo "========================================"
echo "Just Language Installer"
echo "========================================"
echo ""

# 检查权限
if [ "$EUID" -ne 0 ]; then
    echo "Error: Please run with sudo"
    echo "Usage: curl -fsSL http://8.162.0.88/just/install.sh | sudo bash"
    exit 1
fi

INSTALL_DIR="/opt/just"
BIN_DIR="/usr/local/bin"

# 安装依赖
echo "[1/4] Installing dependencies..."
apt-get update -qq 2>/dev/null || yum check-update -q 2>/dev/null || true

if ! command -v java &> /dev/null; then
    echo "      Installing Java..."
    apt-get install -y openjdk-11-jdk-headless 2>/dev/null || yum install -y java-11-openjdk 2>/dev/null
fi

if ! command -v gcc &> /dev/null; then
    echo "      Installing GCC..."
    apt-get install -y build-essential 2>/dev/null || yum groupinstall -y "Development Tools" 2>/dev/null
fi

if ! command -v wget &> /dev/null; then
    apt-get install -y wget 2>/dev/null || yum install -y wget 2>/dev/null
fi

echo "      Dependencies OK"

# 下载 Just SDK
echo "[2/4] Downloading Just SDK..."
rm -rf "$INSTALL_DIR"
mkdir -p "$INSTALL_DIR"

# 从服务器下载
wget -q http://8.162.0.88/just/just-sdk.tar.gz -O /tmp/just-sdk.tar.gz
tar -xzf /tmp/just-sdk.tar.gz -C "$INSTALL_DIR" --strip-components=1
rm /tmp/just-sdk.tar.gz

echo "      Downloaded to $INSTALL_DIR"

# 创建命令
echo "[3/4] Installing commands..."

cat > "$BIN_DIR/justc" << 'JUSTC_EOF'
#!/bin/bash
JUST_HOME="/opt/just"
SOURCE="$1"

if [ -z "$SOURCE" ]; then
    echo "Usage: justc <file.just>"
    exit 1
fi

BASENAME="${SOURCE%.just}"
echo "Compiling $SOURCE..."

java -cp "$JUST_HOME/compiler/bin" just.JustCompiler "$SOURCE" >/dev/null 2>&1 || exit 1
cp "$BASENAME.h" "just_generated.h" 2>/dev/null
gcc -o "$BASENAME" "$BASENAME.c" "$JUST_HOME/runtime/runtime.c" -I. -I"$JUST_HOME/runtime" -O2 >/dev/null 2>&1 || exit 1
rm -f "$BASENAME.c" "$BASENAME.h" "just_generated.h"

echo "Success: $BASENAME"
JUSTC_EOF

cat > "$BIN_DIR/just" << 'JUST_EOF'
#!/bin/bash
PROGRAM="$1"

if [ -z "$PROGRAM" ]; then
    echo "Usage: just <program>"
    exit 1
fi

if [ ! -f "$PROGRAM" ]; then
    echo "Error: $PROGRAM not found"
    exit 1
fi

./"$PROGRAM"
JUST_EOF

chmod +x "$BIN_DIR/justc" "$BIN_DIR/just"
echo "      Commands installed"

# 测试
echo "[4/4] Testing..."
cd /tmp
cat > test.just << 'TEST_EOF'
class Main {
    void main() {
        println("Just works!");
    }
}
TEST_EOF

justc test.just >/dev/null 2>&1
if [ -f "test" ]; then
    ./test
    rm -f test test.just
    echo "      Test OK"
fi

echo ""
echo "========================================"
echo "Installation Complete!"
echo "========================================"
echo ""
echo "Try it now:"
echo "  justc hello.just"
echo "  just hello"
echo ""
