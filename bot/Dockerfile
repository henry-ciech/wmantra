FROM maven:latest AS build

WORKDIR /app

COPY pom.xml ./

COPY templater/pom.xml templater/
COPY bot/pom.xml bot/

RUN mvn dependency:go-offline -DskipAdvancedDependencyResolution
RUN mvn dependency:resolve-plugins -DskipAdvancedDependencyResolution
RUN mvn dependency:analyze -DskipAdvancedDependencyResolution
RUN mvn versions:display-parent-updates -DskipAdvancedDependencyResolution
RUN mvn versions:display-property-updates -DskipAdvancedDependencyResolution

COPY .env .
COPY bot/ bot/

RUN mvn -f bot/pom.xml clean package -DskipTests -Dpmd.skip=true -DskipAdvancedDependencyResolution

FROM eclipse-temurin:17
WORKDIR /app

RUN mkdir -p LOGGING_PATH

COPY --from=build /app/bot/target/*.jar bot.jar
RUN printf '#!/bin/bash\n\njava -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar bot.jar\n' > starter.sh
RUN chmod 755 starter.sh
