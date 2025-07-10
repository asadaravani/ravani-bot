#!/bin/bash

set -e  # Stop the script if any command fails

APP_NAME="ravani-bot-0.0.1-SNAPSHOT.jar"
JAR_PATH="target/$APP_NAME"
LOG_FILE="app.log"

XMS=512m
XMX=2g

echo "Stopping existing application..."
pkill -f "$APP_NAME" || echo "No running app found."

echo "Pulling latest changes from GitHub..."
git reset --hard HEAD && git pull origin main

echo "Building the application..."
./mvnw clean install -DskipTests

if [ ! -f "$JAR_PATH" ]; then
  echo "Build failed: $JAR_PATH not found."
  exit 1
fi

echo "Setting LD_LIBRARY_PATH for native libs..."
export LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu:/usr/local/lib:$LD_LIBRARY_PATH

echo "Starting the application with -Xms$XMS -Xmx$XMX ..."
nohup java -Xms$XMS -Xmx$XMX \
  -Djna.library.path=/usr/lib/x86_64-linux-gnu \
  -jar "$JAR_PATH" > "$LOG_FILE" 2>&1 &

APP_PID=$!
echo -1000 > "/proc/$APP_PID/oom_score_adj"

echo "Application started with PID $APP_PID"
echo "Logs: tail -f $LOG_FILE"
echo "Deployment finished successfully!"