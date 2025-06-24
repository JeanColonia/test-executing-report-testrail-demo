# Etapa 1: Construcción del JAR usando Maven Wrapper
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

# Copia del wrapper y archivos de proyecto
COPY . .

# Compilación del proyecto (omite tests)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Imagen final con Chrome 123 y dependencias
FROM eclipse-temurin:17-jdk

ENV CHROME_VERSION=123.0.6312.86

# Instalar dependencias necesarias para Chrome y Selenium
RUN apt-get update && apt-get install -y \
    wget unzip curl gnupg ca-certificates \
    fonts-liberation libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 \
    libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 \
    libxrandr2 xdg-utils libxss1 libgtk-3-0 libxi6 libxtst6 libxext6 libxfixes3 libudev1 \
    --no-install-recommends && rm -rf /var/lib/apt/lists/*

# Instalar Chrome 123
RUN wget https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}/linux64/chrome-linux64.zip && \
    unzip chrome-linux64.zip && \
    mv chrome-linux64 /opt/chrome && \
    ln -s /opt/chrome/chrome /usr/bin/google-chrome && \
    rm chrome-linux64.zip

# Instalar ChromeDriver 123
RUN wget https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm -rf chromedriver-linux64*

# Establecer variables de entorno necesarias
ENV CHROME_BIN=/usr/bin/google-chrome
ENV PATH=$PATH:/usr/bin

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
