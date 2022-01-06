FROM openjdk:11

WORKDIR /app

COPY ./target/ApiCinema-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]