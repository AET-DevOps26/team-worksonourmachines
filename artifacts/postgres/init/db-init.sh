#!/usr/bin/env sh
set -eu

: "${KEYCLOAK_DB:=keycloak}"
: "${KEYCLOAK_DB_USER:=keycloak}"
: "${KEYCLOAK_DB_PASSWORD:=keycloak}"

: "${STUDENT_DB:=student}"
: "${STUDENT_DB_USER:=student}"
: "${STUDENT_DB_PASSWORD:=student}"

: "${MARKETPLACE_DB:=marketplace}"
: "${MARKETPLACE_DB_USER:=marketplace}"
: "${MARKETPLACE_DB_PASSWORD:=marketplace}"

: "${COMMUNICATION_DB:=communication}"
: "${COMMUNICATION_DB_USER:=communication}"
: "${COMMUNICATION_DB_PASSWORD:=communication}"

create_db_and_user() {
  db_name="$1"
  db_user="$2"
  db_password="$3"

  psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname "$POSTGRES_DB" \
    --set db_name="$db_name" \
    --set db_user="$db_user" \
    --set db_password="$db_password" <<'EOSQL'
SELECT 'CREATE USER "' || :'db_user' || '" WITH PASSWORD ' || quote_literal(:'db_password')
WHERE NOT EXISTS (
  SELECT FROM pg_catalog.pg_roles WHERE rolname = :'db_user'
)
\gexec

SELECT 'CREATE DATABASE "' || :'db_name' || '" OWNER "' || :'db_user' || '"'
WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = :'db_name'
)
\gexec

GRANT ALL PRIVILEGES ON DATABASE :"db_name" TO :"db_user";
EOSQL

  psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname "$db_name" \
    --set db_user="$db_user" <<'EOSQL'
GRANT ALL ON SCHEMA public TO :"db_user";
ALTER SCHEMA public OWNER TO :"db_user";
EOSQL
}

create_db_and_user "$KEYCLOAK_DB" "$KEYCLOAK_DB_USER" "$KEYCLOAK_DB_PASSWORD"
create_db_and_user "$STUDENT_DB" "$STUDENT_DB_USER" "$STUDENT_DB_PASSWORD"
create_db_and_user "$MARKETPLACE_DB" "$MARKETPLACE_DB_USER" "$MARKETPLACE_DB_PASSWORD"
create_db_and_user "$COMMUNICATION_DB" "$COMMUNICATION_DB_USER" "$COMMUNICATION_DB_PASSWORD"
