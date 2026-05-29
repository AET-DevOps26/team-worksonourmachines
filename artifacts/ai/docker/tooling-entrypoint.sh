#!/bin/sh
set -eu

if [ "$#" -eq 0 ]; then
	echo "No command provided"
	exit 1
fi

if [ "$1" = "run" ]; then
	shift
	case "${1:-}" in
	format)
		shift
		exec ruff format "$@" .
		;;
	lint)
		shift
		ruff check "$@" .
		exec ruff format --check "$@" .
		;;
	test)
		shift
		exec pytest "$@"
		;;
	*)
		exec "$@"
		;;
	esac
fi

exec "$@"
