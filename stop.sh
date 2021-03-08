#! /bin/bash

BASE_DIR=$(pwd)

if [[ "${1}" == 'dev' || "${1}" == 'test' || "${1}" == 'prod' ]]; then
  STAGE=${1}
else
  echo "Using default environment: dev"
  STAGE='dev'
fi

ENVIRONMENT_FILE=${BASE_DIR}/config/.env.${STAGE}

if [[ "${STAGE}" == 'dev' ]]; then
  docker-compose \
    --env-file "${ENVIRONMENT_FILE}" \
    down -v
elif [[ "${STAGE}" == 'test' || "${STAGE}" == 'prod' ]]; then
  docker-compose \
    --env-file "${ENVIRONMENT_FILE}" \
    -f docker-compose.yml \
    -f docker-compose.${STAGE}.yml \
    down -v
fi