#!/bin/bash

IMAGE_TAG="kars"
TARGET_TAG="koog-agentcore-runtime-sample-repository"

docker images | grep ${TARGET_TAG} | awk '{print $3}' | xargs -r docker rmi -f
docker build -t ${IMAGE_TAG} .