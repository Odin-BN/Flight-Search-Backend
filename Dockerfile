#Image of Gradle for build
#FROM gradle:7.4-jdk17 AS build

#Work directory
#WORKDIR /app

#Copy the files
#COPY . .

#Generate the .jar file
#RUN gradle build --no-daemon

#Use image for OpenJDK 17
FROM openjdk:17-jdk-slim

#Work directory
WORKDIR /app

#Copy the file to compile on Gradle
COPY build/libs/*.jar app.jar
#COPY --from=build /app/build/libs/*.jar app.jar
COPY .env /app/.env
#Port where the application is executing
EXPOSE 9090

#Execute application
CMD ["java", "-jar", "app.jar"]