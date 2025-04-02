FROM openjdk:23-jdk
WORKDIR /app

# Copy only the built artifact
COPY  build/libs/SIGMS-all.jar ./SIGMS.jar

# Expose the port Ktor runs on
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8000/health || exit 1

# Start the application
ENTRYPOINT ["java", "-jar", "SIGMS.jar"]