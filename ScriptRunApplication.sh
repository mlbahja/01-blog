#!/bin/bash

set -e  # stop the script if any command fails

cd blogger

if [ "$1" = "--reset-db" ]; then
  docker-compose down -v
else
  docker-compose down
fi

docker-compose up -d --build
