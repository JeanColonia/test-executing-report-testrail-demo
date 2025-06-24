# Etapa 1: Construcción del proyecto
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copiamos todo el contenido y construimos el JAR
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Imagen final con Chrome y entorno de ejecución
FROM eclipse-temurin:17-jdk

# Variables de entorno
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

# Instalación de Google Chrome y dependencias para Selenium
RUN apt-get update && apt-get install -y \
    wget unzip curl gnupg ca-certificates \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 libnspr4 libnss3 \
    libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 xdg-utils \
    --no-install-recommends && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && apt-get install -y google-chrome-stable && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Carpeta de trabajo
WORKDIR /app

# Copiamos el JAR desde la imagen anterior
COPY --from=builder /app/target/test-executing-report-testrail-demo-1.0-SNAPSHOT.jar test-executing-report-testrail-demo.jar

# Expone el puerto para el backend (8080 por defecto en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-Dwebdriver.chrome.driver=/usr/bin/google-chrome", "-jar", "test-executing-report-testrail-demo.jar"]

