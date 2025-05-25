FROM eclipse-temurin:17-jdk

ENV TZ=Asia/Seoul

WORKDIR /app

COPY build/libs/app.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]