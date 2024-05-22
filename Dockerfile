FROM openjdk:11 as build
WORKDIR internship

COPY . .

RUN ./gradlew build


FROM openjdk:11
WORKDIR internship

COPY --from=build /internship/build/libs/*.jar /internship/app.jar
EXPOSE 8080

CMD ["java", "-jar", "/internship/app.jar"]