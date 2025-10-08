# Usar una imagen base de OpenJDK 17 con JDK y JRE
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/srvapidataregister-0.0.1-SNAPSHOT.jar app.jar

# Render pone PORT (por defecto 10000); no hardcodees 8080
# EXPOSE es informativo, pero dejamos 10000 para coherencia
EXPOSE 10000

# Arranca Spring en el puerto que Render define
CMD ["sh","-c","exec java -Dserver.port=$PORT -XX:MaxRAMPercentage=75 -jar /app/app.jar"]