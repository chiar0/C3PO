if [ -z "$1" ]; then
  echo "Usage: $0 <message>"
  exit 1
fi
MESSAGE="$1"

API_URL='http://localhost:9800/chat'

curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"$MESSAGE\"}" | jq -r '.reply' | mdless