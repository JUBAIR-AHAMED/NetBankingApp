# Use Maven to build the WAR
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Set the working directory (change "NetBanking" if needed)
WORKDIR /NetBanking

# Copy the project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Use Tomcat to run the app
FROM tomcat:9.0

# Copy the built WAR file to Tomcat's webapps directory
COPY --from=builder /NetBanking/target/NetBanking-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
