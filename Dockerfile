# Use official Maven image to build the WAR
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the WAR
COPY src ./src
RUN mvn package -DskipTests

# Use Tomcat to run the app
FROM tomcat:9.0

# Copy the WAR file from the builder stage
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
