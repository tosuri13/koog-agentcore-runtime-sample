#!/bin/bash

IMAGE_TAG="kars"

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_URI=${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com
REPOSITORY_URI=${ECR_URI}/koog-agentcore-runtime-sample-repository

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "${ECR_URI}"
docker tag ${IMAGE_TAG}:latest ${REPOSITORY_URI}:latest

docker push ${REPOSITORY_URI}:latest
echo "Docker image ${IMAGE_TAG}:latest has been pushed to ECR repository."