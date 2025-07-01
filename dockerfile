#  Using a base image with Java
FROM openjdk:17-jdk-slim

#  Set working directory
WORKDIR /app

#  Copy the built jar into the container
COPY target/game2048api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port app runs on
EXPOSE 8080

# Set the entry point to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
