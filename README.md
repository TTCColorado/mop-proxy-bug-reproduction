# MoP Proxy on cluster Pulsar

This reproduces an issue with MoP Proxy on cluster Pulsar. This is done by
creating a minikube cluster of 3 nodes, and then installing Pulsar with MoP
enabled, and MoP proxy configured. Once this is done, you can forward the
MoP proxy port to localhost and run the provided Java example. For more
insight you can watch the packets sent and received by the client with
wireshark like so `sudo wireshark -i loopback -f mqtt -k`.

## Requirements

Install:

- minikube
- helm

Add the following helm repos:

- `helm repo add jetstack https://charts.jetstack.io --force-update`
- `helm repo add apache https://pulsar.apache.org/charts`

Update helm repos: `helm repo update`

## Setup

1. Run the `./minikube-up.sh` script. This will setup minikube, installing the helm charts
   with the appropriate configurations. This will create a Pulsar cluster with 2 brokers
   which is the minimal amount of brokers to reproduce the connection failure.
2. Wait for the cluster to become available.
3. Forward the MoP proxy port to localhost: `kubectl port-forward pod/pulsar-broker-1 1883:5682`
4. Run the provided Java example `./test-client`, observe the client connection fail after a few seconds.

## Things to try

You can move the sleep till after all 3 subscriptions have been made, and you will see in
wireshark that you get 3 PINGRESP for every PINGREQ. You can move the sleep till before the
subscriptions have been made and you will see nothing responds to the PINGREQ because there
aren't any adapter channels available yet, therefore nothing to forward PINGREQ to.
