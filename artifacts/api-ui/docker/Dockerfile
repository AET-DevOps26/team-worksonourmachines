# syntax=docker/dockerfile:1.7

FROM scalarapi/api-reference:latest

USER root

COPY artifacts/api-ui/docker/entrypoint.sh /config/entrypoint.sh
COPY artifacts/api-ui/scalar.config.json /config/scalar.config.json
COPY api/specs /docs

RUN chmod +x /config/entrypoint.sh \
  && chown -R caddy:caddy /docs /config

USER caddy

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "/config/entrypoint.sh"]
