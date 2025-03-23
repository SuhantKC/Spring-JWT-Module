FROM openjdk:17-alpine
WORKDIR /app
COPY build/libs/Auth-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java","-jar","app.jar"]