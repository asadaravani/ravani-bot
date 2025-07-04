#!/bin/bash

echo "Stopping existing application..."
pkill -f 'java -jar' || echo "No running app found."

echo "Pulling latest changes from GitHub..."
git pull origin main

echo "Building the application..."
./mvnw clean install -DskipTests

echo "Setting LD_LIBRARY_PATH for native libs..."
export LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu:/usr/local/lib:$LD_LIBRARY_PATH

echo "Starting the application..."
nohup java -Djna.library.path=/usr/lib/x86_64-linux-gnu -jar target/ravani-bot-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
echo "Application deployed successfully!"