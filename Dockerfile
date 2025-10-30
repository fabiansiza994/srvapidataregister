# JDK 17 slim (Debian-based)
FROM openjdk:17-jdk-slim

# Evita prompts en apt
ENV DEBIAN_FRONTEND=noninteractive
# Fuerza headless en la JVM (además del flag en CMD)
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

# Instala fontconfig + una familia de fuentes (DejaVu)
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig fonts-dejavu tzdata \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/srvapidataregister-0.0.1-SNAPSHOT.jar app.jar

# Render define PORT (p.ej. 10000)
EXPOSE 10000

# Arranque: puerto de Render + headless explícito por si acaso
CMD ["sh","-c","exec java -Dserver.port=$PORT -Djava.awt.headless=true -Dspring.main.headless=true -XX:MaxRAMPercentage=75 -jar /app/app.jar"]