#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"

echo "Running pre-commit hooks in ${REPO_ROOT}"
make -C "${REPO_ROOT}" lint
