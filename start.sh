#! /bin/bash

BASE_DIR=$(pwd)
TLS_DIR=${BASE_DIR}/tls

KEY_SIZE=${3:-4096}

SERVERS_NAME=${4:-servers}
SERVERS_ALIAS=${5:-servers}
SERVERS_KEYSTORE_PASSWORD=${6:-password}

CLIENT_NAME=${7:-client}
CLIENT_ALIAS=${8:-client}
CLIENT_KEYSTORE_PASSWORD=${9:-password}

RMQ_NAME=${10:-rabbitmq}

function generateEnvironmentFile() {
  # sed on MacOSX does not support -i flag with a null extension. We will use
  # 't' for our back-up's extension and delete it at the end of the function
  ARCH=$(uname -s | grep Darwin)
  if [[ "$ARCH" == "Darwin" ]]; then
    OPTS="-it"
  else
    OPTS="-i"
  fi

  # Copy the template to the file that will be modified to add certificates
  cp "${ENVIRONMENT_TEMPLATE}" "${ENVIRONMENT_FILE}" || exit

  if [[ $tls_enabled == "true" ]]; then
    TLS_ENABLED=true
    FRONTEND_APP_VERSION=v1.1.0
    RMQ_PORT=5671
    RMQ_MANAGER_PORT=15671
  else
    TLS_ENABLED=false
    FRONTEND_APP_VERSION=v1.0.0
    RMQ_PORT=5672
    RMQ_MANAGER_PORT=15672
    sed ${OPTS} \
      -e 's/RABBITMQ_SSL_CACERTFILE.*//' \
      -e 's/RABBITMQ_SSL_CERTFILE.*//' \
      -e 's/RABBITMQ_SSL_FAIL_IF_NO_PEER_CERT.*//' \
      -e 's/RABBITMQ_SSL_KEYFILE.*//' \
      -e 's/RABBITMQ_SSL_VERIFY.*//' \
      "${ENVIRONMENT_FILE}"
  fi

  # The next steps will replace the template's contents with the
  # actual values of the certificates and private key files
  sed ${OPTS} \
    -e "s/SERVERS_NAME/${SERVERS_NAME}/g" \
    -e "s/SERVERS_ALIAS/${SERVERS_ALIAS}/g" \
    -e "s/SERVERS_KEYSTORE_PASSWORD/${SERVERS_KEYSTORE_PASSWORD}/g" \
    -e "s/RMQ_NAME/${RMQ_NAME}/g" \
    -e "s/{TLS_ENABLED}/${TLS_ENABLED}/g" \
    -e "s/{FRONTEND_APP_VERSION}/${FRONTEND_APP_VERSION}/g" \
    -e "s/{RMQ_PORT}/${RMQ_PORT}/g" \
    -e "s/{RMQ_MANAGER_PORT}/${RMQ_MANAGER_PORT}/g" \
    "${ENVIRONMENT_FILE}"

  if [[ "$ARCH" == "Darwin" ]]; then
    rm "${ENVIRONMENT_FILE}t"
  fi
}

function generateCertificates() {
  # Clean up previous generated certificates
  rm -rf "${TLS_DIR}"/ca "${TLS_DIR}"/certs "${TLS_DIR}"/crl

  docker run \
    -it \
    --rm \
    --entrypoint sh \
    --mount "type=bind,source=${TLS_DIR},destination=/export" \
    --user "$(id -u):$(id -g)" \
    danijelradakovic/openssl-keytool \
    gen.sh "${KEY_SIZE}" \
    "${SERVERS_NAME}" "${SERVERS_ALIAS}" "${SERVERS_KEYSTORE_PASSWORD}" \
    "${CLIENT_NAME}" "${CLIENT_ALIAS}" "${CLIENT_KEYSTORE_PASSWORD}" \
    "${RMQ_NAME}"
}

show_help() {
  echo "Available options:"
  printf "\t%s\n" "-g  generate certificates"
  printf "\t%s\n" "-t  enable TLS for message broker and application"
}

#----------PARSE OPTIONS AND ARGUMENTS----------
# Reset in case getopts has been used previously in the shell.
OPTIND=1

generate_cert=false
tls_enabled=false

options_string="gth"
while getopts $options_string opt; do
  case "$opt" in
  g)
    generate_cert=true
    ;;
  t)
    tls_enabled=true
    ;;
  h | ?)
    show_help
    exit 1
    ;;
  esac
done

shift $((OPTIND - 1))
[ "${1:-}" = "--" ] && shift

if [[ "${1}" == 'dev' || "${1}" == 'test' || "${1}" == 'prod' ]]; then
  STAGE=${1}
else
  echo "Using default environment: dev"
  STAGE='dev'
fi
#-----------------------------------------------

ENVIRONMENT_FILE=${BASE_DIR}/config/.env.${STAGE}
ENVIRONMENT_TEMPLATE=${BASE_DIR}/templates/.env.${STAGE}

if [[ $generate_cert == "true" ]]; then
  generateCertificates
fi

generateEnvironmentFile

if [[ "${STAGE}" == 'dev' ]]; then
  docker-compose \
    --env-file "${ENVIRONMENT_FILE}" \
    up --build
elif [[ "${STAGE}" == 'test' || "${STAGE}" == 'prod' ]]; then
  docker-compose \
    --env-file "${ENVIRONMENT_FILE}" \
    -f docker-compose.yml \
    -f docker-compose.${STAGE}.yml \
    up --build
fi
