#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status

echo "Stopping existing application..."
pkill -f 'java -jar' || echo "No running app found."

echo "Navigating to app directory..."
cd /path/to/your/app || exit 1

echo "Pulling latest changes from GitHub..."
git reset --hard
git pull origin main

echo "Building the application..."
./mvnw clean install -DskipTests

echo "Starting the application..."
nohup java -jar target/ravani-bot-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "Application deployed successfully!"