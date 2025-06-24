# Etapa 1: Construcción del JAR usando Maven Wrapper
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

# Copia del wrapper y archivos de proyecto
COPY . .

# Si usas Maven Wrapper, asegúrate de tener ./mvnw y .mvn/
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Imagen final con Chrome y dependencias
FROM eclipse-temurin:17-jdk

# Instalar dependencias requeridas por Chrome y Selenium

RUN apt-get update && apt-get install -y wget unzip curl gnupg ca-certificates \
    fonts-liberation libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 \
    libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 \
    libxrandr2 xdg-utils --no-install-recommends || cat /var/log/apt/term.log


# Agregar la clave y repo de Google Chrome
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-linux-signing-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list

# Instalar Google Chrome
#RUN apt-get update && apt-get install -y google-chrome-stable
# Chrome 137 específicamente
RUN wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_137.0.0.0-1_amd64.deb \
    && apt install -y ./google-chrome-stable_137.0.0.0-1_amd64.deb \
    && rm google-chrome-stable_137.0.0.0-1_amd64.deb


# Limpieza para reducir peso de la imagen
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Comando para ejecutar el servicio
ENTRYPOINT ["java", "-jar", "app.jar"]
