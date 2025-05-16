FROM openjdk:11
WORKDIR internship

COPY ./build/libs/internship-management-0.0.1-SNAPSHOT.jar /internship/app.jar
EXPOSE 8080

CMD ["java", "-jar", "/internship/app.jar"]