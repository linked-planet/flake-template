#!/usr/bin/env bash
set -e

DOCKER_TAGS=$@

DOCKER_REGISTRY_NAME=mkportal
DOCKER_PROJECT_NAME=order-processing
DOCKER_IMAGE_NAME=${DOCKER_REGISTRY_NAME}/${DOCKER_PROJECT_NAME}
AWS_REGISTRY_URL="528680286259.dkr.ecr.eu-central-1.amazonaws.com"

# aws login
aws ecr get-login-password | docker login --username AWS --password-stdin "${AWS_REGISTRY_URL}"

# create ecr repository if it does not exist
if ! aws ecr list-images --repository-name "${DOCKER_IMAGE_NAME}" >/dev/null 2>/dev/null; then
  echo "INFO: ${AWS_REGISTRY_URL}/${DOCKER_IMAGE_NAME} does not exist, we try to create it"
  aws ecr create-repository --repository-name ${DOCKER_IMAGE_NAME}
fi

echo "INFO: Pushing "
for tag in ${DOCKER_TAGS}
do
  docker tag "${DOCKER_IMAGE_NAME}:latest" "${AWS_REGISTRY_URL}/${DOCKER_IMAGE_NAME}:${tag}"
  docker push "${AWS_REGISTRY_URL}/${DOCKER_IMAGE_NAME}:${tag}"
done

set +e
