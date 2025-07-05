FROM ubuntu:22.04

LABEL authors="beganov"

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    tesseract-ocr \
    libleptonica-dev \
    libtesseract-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/ravani-bot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]