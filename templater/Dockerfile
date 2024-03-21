FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app

COPY .env .
COPY pom.xml ./

COPY templater/ templater/

RUN mvn -f templater/pom.xml clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app

RUN mkdir -p ./logs

COPY --from=build /app/templater/target/*.jar templater.jar
RUN printf '#!/bin/bash\n\njava -jar templater.jar\n' > starter.sh
RUN chmod 755 starter.sh
