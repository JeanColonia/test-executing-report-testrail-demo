# Etapa base para construcción
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests


# Etapa final con Chrome y Selenium
FROM eclipse-temurin:17-jdk

# Instalar dependencias requeridas por Chrome y Selenium
RUN apt-get update && apt-get install -y wget unzip curl gnupg ca-certificates \
    fonts-liberation libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 \
    libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 \
    libxrandr2 xdg-utils --no-install-recommends || cat /var/log/apt/term.log

# Agregar clave y repositorio de Chrome
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | \
    gpg --dearmor -o /usr/share/keyrings/google-linux-signing-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list

# Instalar Chrome versión 137.0.6530.54-1
RUN wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_137.0.6530.54-1_amd64.deb \
    && apt install -y ./google-chrome-stable_137.0.6530.54-1_amd64.deb \
    && rm google-chrome-stable_137.0.6530.54-1_amd64.deb

# Limpieza
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
