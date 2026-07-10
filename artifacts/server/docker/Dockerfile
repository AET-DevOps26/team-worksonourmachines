# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-17 AS tooling

ENV MAVEN_CONFIG=/workspace/.m2

RUN mkdir -p /workspace/.m2/repository /workspace/artifacts/server \
  && chmod -R 0777 /workspace

WORKDIR /workspace/artifacts/server
