# Etapa base con Java 17
FROM eclipse-temurin:17-jdk AS base

# Instala dependencias del sistema requeridas por Chrome y Selenium
RUN apt-get update && \
    apt-get install -y \
    wget unzip curl gnupg ca-certificates \
    fonts-liberation libappindicator3-1 libasound2 \
    libatk-bridge2.0-0 libatk1.0-0 libcups2 \
    libdbus-1-3 libgdk-pixbuf2.0-0 libnspr4 libnss3 \
    libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 \
    xdg-utils --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Descargar e instalar Chrome estable desde .deb oficial
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb

# Crear directorio de trabajo para la app
WORKDIR /app

# Copiar el JAR generado por Maven (asegúrate de que el build lo genera)
COPY target/*.jar app.jar

# Puerto (opcional para Render/Koyeb)
EXPOSE 8080

# Ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
