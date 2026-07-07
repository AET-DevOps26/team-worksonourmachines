FROM node:24-bookworm

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV PNPM_STORE_DIR="/pnpm/store"
ENV COREPACK_HOME=/pnpm/corepack
RUN mkdir -p $COREPACK_HOME && corepack enable && corepack prepare pnpm@10.28.2 --activate && chmod -R a+rwX $COREPACK_HOME

RUN apt-get update \
	&& apt-get install -y --no-install-recommends openjdk-17-jre-headless=17.0.19+10-1~deb12u2 \
	&& rm -rf /var/lib/apt/lists/*

COPY api/docker/tooling-entrypoint.sh /usr/local/bin/tooling-entrypoint
RUN chmod +x /usr/local/bin/tooling-entrypoint

WORKDIR /workspace/api

ENTRYPOINT ["tooling-entrypoint"]
CMD ["run", "generate"]
