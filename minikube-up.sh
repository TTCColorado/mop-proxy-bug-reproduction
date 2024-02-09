#!/bin/bash
minikube start --nodes 3 --driver=docker --memory 8192 --cpus 4

helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.14.2 \
  --set installCRDs=true

helm install pulsar -f pulsar-cluster-values.yaml apache/pulsar
