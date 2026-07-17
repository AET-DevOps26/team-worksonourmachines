# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-17 AS tooling

ENV MAVEN_CONFIG=/workspace/.m2

RUN mkdir -p /workspace/.m2/repository /workspace/artifacts/server \
  && chmod -R 0777 /workspace

WORKDIR /workspace/artifacts/server

FROM tooling AS build

ARG SERVER_MODULE

COPY artifacts/server/pom.xml ./
COPY artifacts/server/common ./common
COPY artifacts/server/${SERVER_MODULE} ./${SERVER_MODULE}

RUN test -n "${SERVER_MODULE}" \
  && sed -i "/<module>/!b;/<module>common<\/module>/b;/<module>${SERVER_MODULE}<\/module>/b;d" pom.xml \
  && mvn -q -pl "${SERVER_MODULE}" -am package -DskipTests \
  && jar_file="$(find "${SERVER_MODULE}/target" -maxdepth 1 \
      -name "server-${SERVER_MODULE}-*.jar" \
      ! -name "*-sources.jar" \
      ! -name "*.original" \
      -print -quit)" \
  && test -n "${jar_file}" \
  && cp "${jar_file}" /workspace/server.jar

FROM eclipse-temurin:17-jre-jammy AS prod

RUN groupadd --gid 10001 app \
  && useradd --uid 10001 --gid 10001 --create-home --shell /usr/sbin/nologin app

WORKDIR /app

COPY --from=build --chown=app:app /workspace/server.jar ./server.jar

USER app

ENTRYPOINT ["java", "-jar", "/app/server.jar"]
