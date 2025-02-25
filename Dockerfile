# Use an official Tomcat image  
FROM tomcat:9.0

# Copy your WAR file to the Tomcat webapps directory  
RUN mkdir -p target  # Ensure the directory exists
COPY target/*.war /usr/local/tomcat/webapps/

# Expose port 8080 for external access  
EXPOSE 8080

# Start Tomcat  
CMD ["catalina.sh", "run"]x