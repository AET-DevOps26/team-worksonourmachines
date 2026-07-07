#!/bin/sh
set -eu

if [ -f /config/scalar.config.json ]; then
  export API_REFERENCE_CONFIG="$(cat /config/scalar.config.json)"
fi

exec /usr/local/bin/start.sh
