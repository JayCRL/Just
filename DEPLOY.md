# Simple Server Deployment Guide

## Quick Deploy (Manual)

### Step 1: Package SDK
```powershell
cd C:\Users\25566\just-lang
tar -czf just-sdk.tar.gz compiler runtime examples docs README.md LICENSE VERSION.txt
```

### Step 2: Upload to Server
```bash
# Using SCP (from Git Bash or WSL)
scp just-sdk.tar.gz root@8.162.0.88:/tmp/
scp install-online.sh root@8.162.0.88:/tmp/
```

### Step 3: Setup on Server
```bash
# SSH to server
ssh root@8.162.0.88

# Create web directory
mkdir -p /var/www/html/just
mv /tmp/just-sdk.tar.gz /var/www/html/just/
mv /tmp/install-online.sh /var/www/html/just/install.sh
chmod 644 /var/www/html/just/*

# Install nginx (if not installed)
apt-get update
apt-get install -y nginx
systemctl start nginx
systemctl enable nginx
```

## User Installation

Users can now install Just with one command:

```bash
curl -fsSL http://8.162.0.88/just/install.sh | sudo bash
```

Or:

```bash
wget -qO- http://8.162.0.88/just/install.sh | sudo bash
```

## Test Installation

```bash
# Create test file
echo 'class Main { void main() { println("Hello!"); } }' > hello.just

# Compile
justc hello.just

# Run
just hello
```

Output: `Hello!`

## Alternative: Direct Download

Users can also download and run locally:

```bash
wget http://8.162.0.88/just/install.sh
chmod +x install.sh
sudo ./install.sh
```
