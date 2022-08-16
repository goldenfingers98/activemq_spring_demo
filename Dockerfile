FROM bellsoft/liberica-openjdk-alpine:11.0.15
ADD target/ms-0.0.1-SNAPSHOT.jar /opt
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar /opt/ms-0.0.1-SNAPSHOT.jar"]
