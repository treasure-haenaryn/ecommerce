#!/bin/bash
# start-services.sh로 띄운 5개 서비스를 전부 종료한다.
cd "$(dirname "$0")"

if [ ! -f logs/pids.txt ]; then
  echo "logs/pids.txt가 없음 - 실행 중인 서비스가 없거나 start-services.sh를 먼저 실행 안 함"
  exit 0
fi

while read -r pid; do
  if kill "$pid" 2>/dev/null; then
    echo "종료됨: $pid"
  fi
done < logs/pids.txt

rm -f logs/pids.txt
echo "전체 종료 완료"
