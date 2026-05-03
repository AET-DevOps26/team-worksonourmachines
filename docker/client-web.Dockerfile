FROM node:24-bookworm AS base

WORKDIR /app

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable && corepack prepare pnpm@10.18.0 --activate

COPY artifacts/client-web/package.json artifacts/client-web/pnpm-lock.yaml ./

# -----------------------------

FROM base AS dev

ENV NODE_ENV=development

RUN --mount=type=cache,id=client-web-pnpm,target=/pnpm/store pnpm install --frozen-lockfile

EXPOSE 5173

CMD ["pnpm", "run", "dev"]

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

COPY --from=prod-deps /app/node_modules ./node_modules
COPY --from=build /app/build ./build
COPY --from=build /app/package.json ./package.json

EXPOSE 5173

CMD ["pnpm", "start"]