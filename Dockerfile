FROM node:alpine3.11 AS frontBuild

LABEL maintainer="danijelradakovic@uns.ac.rs"

ARG REPOSITORY_OWNER=DanijelRadakovic
ARG APPLLICATION_REPOSITORY=Servers-Front
ARG APPLICATION_VERSION=v1.1.0

WORKDIR /usr/src
RUN apk --update --no-cache add curl tar && \
    curl -L https://github.com/${REPOSITORY_OWNER}/${APPLLICATION_REPOSITORY}/archive/${APPLICATION_VERSION}.tar.gz | tar -xz && \
    cd ${APPLLICATION_REPOSITORY}* && \
    npm install && \
    npm run build --prod


FROM maven:3.6.3-openjdk-11-slim AS appWebServerBuild
ARG STAGE=dev
WORKDIR /usr/src/server
COPY . .
COPY --from=frontBuild /usr/src/${APPLLICATION_REPOSITORY}*/dist/servers ./src/main/resources/static
RUN mvn package -P${STAGE} -DskipTests


FROM openjdk:11-jre-slim AS appWebServerRuntime
WORKDIR /app
COPY --from=appWebServerBuild /usr/src/server/target/servers.jar ./
EXPOSE 8080
CMD java -jar servers.jar


FROM maven:3.6.3-openjdk-11-slim AS appServerBuild
ARG STAGE=dev
WORKDIR /usr/src/server
COPY . .
RUN mvn package -P${STAGE} -DskipTests


FROM openjdk:11-jre-slim AS appServerRuntime
WORKDIR /app
COPY --from=appServerBuild /usr/src/server/target/servers.jar ./
EXPOSE 8080
CMD java -jar servers.jar
