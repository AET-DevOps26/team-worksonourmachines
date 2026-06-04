#!/usr/bin/env sh
set -eu

: "${KEYCLOAK_DB:=keycloak}"
: "${KEYCLOAK_DB_USER:=keycloak}"
: "${KEYCLOAK_DB_PASSWORD:=keycloak}"

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set keycloak_db="$KEYCLOAK_DB" \
  --set keycloak_user="$KEYCLOAK_DB_USER" \
  --set keycloak_password="$KEYCLOAK_DB_PASSWORD" <<'EOSQL'
CREATE DATABASE :"keycloak_db";
CREATE USER :"keycloak_user" WITH PASSWORD :'keycloak_password';
GRANT ALL PRIVILEGES ON DATABASE :"keycloak_db" TO :"keycloak_user";
EOSQL

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$KEYCLOAK_DB" \
  --set keycloak_user="$KEYCLOAK_DB_USER" <<'EOSQL'
GRANT ALL ON SCHEMA public TO :"keycloak_user";
EOSQL
