#!/bin/bash

docker run \
  -v  "$PWD/docker/mm2.properties:/opt/mm2.properties" \
  -v "$PWD/docker/plugins:/opt/plugins" \
  -v "$PWD/docker/mm2.properties:/etc$/mm2.properties" \
  -it \
  -u root \
   bitnami/kafka /bin/bash
  
#  confluentinc/cp-kafka-connect /usr/bin/connect-mirror-maker /opt/mm2.properties
