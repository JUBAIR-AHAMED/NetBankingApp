# Use Maven with JDK 8 (Alpine) for building
FROM maven:3.8.6-eclipse-temurin-8-alpine AS builder

# Set the working directory
WORKDIR /NetBanking

# Copy the project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Use Alpine-based Tomcat with JDK 11 for runtime
FROM tomcat:9.0-jdk11-alpine

# Copy the built WAR file to Tomcat's webapps directory as ROOT.war
COPY --from=builder /NetBanking/target/NetBanking-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
