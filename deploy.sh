#!/bin/bash

set -e

echo "Pulling latest changes..."
git reset --hard HEAD && git pull origin main

echo "Building the application..."
./mvnw clean install -DskipTests

echo "Restarting the systemd service..."
sudo systemctl daemon-reload
sudo systemctl restart ravani-bot

echo "✅app deployed and restarted via systemd✅"