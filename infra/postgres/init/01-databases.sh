#!/bin/sh
set -eu

create_service_database() {
  role="$1"
  database="$2"
  password_variable="$3"
  eval "password=\${$password_variable}"
  psql --username "$POSTGRES_USER" --dbname postgres --set=ON_ERROR_STOP=1 \
    --set=role="$role" --set=database="$database" --set=password="$password" <<'SQL'
CREATE ROLE :"role" LOGIN PASSWORD :'password';
CREATE DATABASE :"database" OWNER :"role";
SQL
}

create_service_database mcuser DB_USER MCUSER_DB_PASSWORD
create_service_database mcquestion DB_QUESTION MCQUESTION_DB_PASSWORD
create_service_database mcreview DB_REVIEW MCREVIEW_DB_PASSWORD
create_service_database mcaudit DB_AUDIT MCAUDIT_DB_PASSWORD