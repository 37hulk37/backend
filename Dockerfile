FROM amazoncorretto:17-alpine-jdk
EXPOSE 8080
WORKDIR /opt/app
CMD ["./gradlew", "clean", "assemble"]
COPY build/libs/ .
ENTRYPOINT ["java", "-jar", "app"]