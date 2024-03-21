FROM maven:3.9.6-eclipse-temurin-17-focal AS build

WORKDIR /app

COPY pom.xml ./
COPY .env .
COPY bot/ bot/

RUN mvn -f bot/pom.xml clean package -DskipTests -Dpmd.skip=true

FROM eclipse-temurin:17
WORKDIR /app

RUN mkdir -p LOGGING_PATH

COPY --from=build /app/bot/target/*.jar bot.jar
RUN printf '#!/bin/bash\n\njava -jar bot.jar\n' > starter.sh
RUN chmod 755 starter.sh
