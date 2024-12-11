# Use a base image with Java installed
FROM openjdk:17-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the Spring Boot jar file to the container
COPY target/config-server-0.0.1-SNAPSHOT.jar /app/application.jar

# Expose the port the application will run on
EXPOSE 9091

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
