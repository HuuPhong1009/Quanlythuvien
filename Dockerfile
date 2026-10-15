FROM tomcat:9.0-jdk8

# Clear default Tomcat apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy web application to ROOT
COPY web /usr/local/tomcat/webapps/ROOT

# Disable port 8005 so Render health check does not terminate Tomcat
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

# Bind port dynamically from $PORT or default 8080
CMD sed -i 's/port="8080"/port="'"${PORT:-8080}"'"/' /usr/local/tomcat/conf/server.xml && catalina.sh run
