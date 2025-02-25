# Stage 1: Build the WAR file
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Set the working directory
WORKDIR /NetBanking

# Copy the project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application with Tomcat
FROM tomcat:9.0

# Copy the built WAR file to Tomcat's webapps directory
COPY --from=builder /NetBanking/target/NetBanking-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port
EXPOSE 8080

# Start Tomcat with dynamic port binding
CMD ["sh", "-c", "sed -i 's/8080/$PORT/g' /usr/local/tomcat/conf/server.xml && catalina.sh run"]