FROM gradle AS build
RUN gradle build ...

FROM openjdk
COPY --from=build kursach.jar .
RUN java -jar kursach.jar