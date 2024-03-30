#!/bin/bash

ENV_FILE="./.env"

if [ ! -f "$ENV_FILE" ]; then
    echo "The .env file does not exist at $ENV_FILE"
    exit 1
fi

set -a # automatically export all variables that are defined
source "$ENV_FILE"
set +a # stop automatically exporting

echo "All variables from $ENV_FILE have been exported."
