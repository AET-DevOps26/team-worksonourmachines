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
		exec ruff check "$@" .
		;;
	test)
		shift
		exec echo "No tests yet"
		;;
	*)
		exec "$@"
		;;
	esac
fi

exec "$@"
