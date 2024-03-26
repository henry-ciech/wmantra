FROM maven:latest AS build
WORKDIR /app

COPY .env .
COPY pom.xml ./
COPY templater/pom.xml templater/
COPY bot/pom.xml bot/


RUN mvn dependency:go-offline -DskipAdvancedDependencyResolution
RUN mvn dependency:resolve-plugins -DskipAdvancedDependencyResolution
RUN mvn dependency:analyze -DskipAdvancedDependencyResolution
RUN mvn versions:display-parent-updates -DskipAdvancedDependencyResolution
RUN mvn versions:display-property-updates -DskipAdvancedDependencyResolution

COPY templater/ templater/

RUN mvn -f templater/pom.xml clean package -DskipTests -DskipAdvancedDependencyResolution

FROM eclipse-temurin:17
WORKDIR /app

RUN mkdir -p ./logs

COPY --from=build /app/templater/target/*.jar templater.jar
RUN printf '#!/bin/bash\n\njava -jar templater.jar\n' > starter.sh
RUN chmod 755 starter.sh
