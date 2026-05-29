#!/usr/bin/env bash
# generate-keystore.sh
#
# Generates a release keystore and prints the base64 value
# ready to paste into GitHub Actions secrets.
#
# Usage: bash scripts/generate-keystore.sh
#
set -euo pipefail

KEYSTORE_FILE="release.jks"
KEY_ALIAS="${KEY_ALIAS:-lemuroid}"
KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:-$(openssl rand -base64 16)}"
KEY_PASSWORD="${KEY_PASSWORD:-$(openssl rand -base64 16)}"
DNAME="${DNAME:-CN=Lemuroid, OU=Dev, O=Lemuroid, L=Unknown, ST=Unknown, C=US}"
VALIDITY="${VALIDITY:-10000}"

echo ""
echo "=== Generating release keystore ==="
echo "  File        : $KEYSTORE_FILE"
echo "  Alias       : $KEY_ALIAS"
echo ""

keytool -genkeypair \
  -keystore "$KEYSTORE_FILE" \
  -storetype PKCS12 \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity "$VALIDITY" \
  -storepass "$KEYSTORE_PASSWORD" \
  -keypass "$KEY_PASSWORD" \
  -dname "$DNAME"

echo ""
echo "=== GitHub Actions Secrets to add ==="
echo ""
echo "KEYSTORE_BASE64:"
base64 --wrap=0 "$KEYSTORE_FILE"
echo ""
echo ""
echo "KEYSTORE_PASSWORD:  $KEYSTORE_PASSWORD"
echo "KEY_ALIAS:          $KEY_ALIAS"
echo "KEY_PASSWORD:       $KEY_PASSWORD"
echo ""
echo "⚠  Save these values — they cannot be recovered!"
echo "   KEYSTORE_FILE is at: $(pwd)/$KEYSTORE_FILE"
echo "   Add $KEYSTORE_FILE to .gitignore (never commit it)."
