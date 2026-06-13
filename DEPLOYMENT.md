# Just SDK - Server Deployment Guide

## 📦 Deployment Overview

Deploy Just SDK to your web server for one-line installation.

---

## 🚀 Quick Deploy Steps

### 1. Package SDK

```bash
# On Windows (PowerShell)
Compress-Archive -Path compiler,runtime,examples,docs,README.md,LICENSE,VERSION.txt -DestinationPath just-sdk.zip

# Or using tar (Git Bash)
tar -czf just-sdk.tar.gz compiler runtime examples docs README.md LICENSE VERSION.txt
```

### 2. Upload to Server

Upload these files to your web server:
- `just-sdk.zip` (or .tar.gz)
- `install-online.sh` → rename to `install.sh`

Place them in: `/var/www/html/just/`

### 3. Configure Web Server

```bash
# Install nginx (if needed)
apt-get update
apt-get install -y nginx

# Start nginx
systemctl start nginx
systemctl enable nginx

# Set permissions
chmod 644 /var/www/html/just/*
```

---

## 🎯 User Installation

Users can install with:

```bash
curl -fsSL http://YOUR_SERVER/just/install.sh | sudo bash
```

Or:

```bash
wget -qO- http://YOUR_SERVER/just/install.sh | sudo bash
```

---

## ✅ Verify Deployment

Test in browser:
```
http://YOUR_SERVER/just/install.sh
```

Should display the installation script.

---

## 📝 Installation Script Features

The `install-online.sh` script automatically:
- Installs Java 11
- Installs GCC
- Downloads Just SDK
- Creates `justc` and `just` commands
- Runs verification test

---

## 🧪 Test Installation

After users install:

```bash
echo 'class Main { void main() { println("Works!"); } }' > test.just
justc test.just
just test
```

Output: `Works!`

---

**Simple, Fast, One-Line Installation!** 🚀
