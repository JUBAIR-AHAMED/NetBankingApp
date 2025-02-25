# Use official Maven image to build the WAR
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the WAR file
RUN mvn clean package -DskipTests

# Use Tomcat to run the app
FROM tomcat:9.0

# Copy the built WAR file and rename it to ROOT.war for direct access
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]