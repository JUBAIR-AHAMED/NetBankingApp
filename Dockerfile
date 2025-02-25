# Use Tomcat as the base image
FROM tomcat:9.0

# Set working directory inside Tomcat
WORKDIR /usr/local/tomcat/webapps/

# Copy the pre-built WAR file from the repository to Tomcat
COPY NetBanking/target/NetBanking-0.0.1-SNAPSHOT.war ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
