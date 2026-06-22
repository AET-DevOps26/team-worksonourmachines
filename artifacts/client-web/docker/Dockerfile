# syntax=docker/dockerfile:1.7

FROM node:24-bookworm AS base

WORKDIR /app

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV PNPM_STORE_DIR="/pnpm/store"
ENV COREPACK_HOME=/pnpm/corepack
RUN mkdir -p $COREPACK_HOME && corepack enable && corepack prepare pnpm@10.18.0 --activate && chmod -R a+rwX $COREPACK_HOME

COPY artifacts/client-web/package.json artifacts/client-web/pnpm-lock.yaml ./

# -----------------------------

FROM base AS dev

ENV NODE_ENV=development

COPY artifacts/client-web/docker/entrypoint.sh /usr/local/bin/dev-entrypoint
RUN chmod +x /usr/local/bin/dev-entrypoint

EXPOSE 5173

ENTRYPOINT ["dev-entrypoint"]
CMD ["run", "dev"]

# -----------------------------

FROM base AS tooling

COPY artifacts/client-web/docker/entrypoint.sh /usr/local/bin/tooling-entrypoint
RUN chmod +x /usr/local/bin/tooling-entrypoint

WORKDIR /workspace/artifacts/client-web

ENTRYPOINT ["tooling-entrypoint"]
CMD ["run", "lint"]

# -----------------------------

FROM base AS build

ENV NODE_ENV=production

RUN --mount=type=cache,id=client-web-pnpm,target=/pnpm/store pnpm install --frozen-lockfile --prefer-offline

COPY artifacts/client-web ./

RUN pnpm build

# -----------------------------

FROM base AS prod-deps

ENV NODE_ENV=production

RUN --mount=type=cache,id=client-web-pnpm,target=/pnpm/store pnpm install --prod --frozen-lockfile --prefer-offline

# -----------------------------

FROM node:24-bookworm-slim AS prod

WORKDIR /app

ENV NODE_ENV=production
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV PNPM_STORE_DIR="/pnpm/store"
ENV COREPACK_HOME=/pnpm/corepack
RUN mkdir -p $COREPACK_HOME && corepack enable && corepack prepare pnpm@10.18.0 --activate && chmod -R a+rwX $COREPACK_HOME

COPY --from=prod-deps /app/node_modules ./node_modules
COPY --from=build /app/build ./build
COPY --from=build /app/package.json ./package.json

EXPOSE 5173

CMD ["pnpm", "start"]