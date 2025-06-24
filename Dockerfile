FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---------------------------------------


FROM eclipse-temurin:17-jdk

# Instalación de Chrome y dependencias
RUN apt-get update && \
    apt-get install -y wget curl gnupg2 ca-certificates --no-install-recommends && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y \
        google-chrome-stable \
        unzip \
        fonts-liberation \
        libappindicator3-1 \
        libasound2 \
        libatk-bridge2.0-0 \
        libatk1.0-0 \
        libcups2 \
        libdbus-1-3 \
        libgdk-pixbuf2.0-0 \
        libnspr4 \
        libnss3 \
        libx11-xcb1 \
        libxcomposite1 \
        libxdamage1 \
        libxrandr2 \
        xdg-utils \
        --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# ---------------------------------------

WORKDIR /app

# Copiamos el JAR desde la imagen anterior
COPY --from=builder /app/target/test-executing-report-testrail-demo-1.0-SNAPSHOT.jar test-executing-report-testrail-demo.jar

# Expone el puerto para el backend (8080 por defecto en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-Dwebdriver.chrome.driver=/usr/bin/google-chrome", "-jar", "test-executing-report-testrail-demo.jar"]

