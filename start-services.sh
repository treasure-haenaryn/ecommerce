#!/bin/bash
# 5개 서비스를 한 번에 백그라운드로 기동하고, 로그는 각각 별도 파일로 남긴다.
set -e
cd "$(dirname "$0")"

mkdir -p logs

SERVICES=("order-service" "payment-service" "inventory-service" "shipping-service" "notification-service")

for svc in "${SERVICES[@]}"; do
  echo "starting $svc..."
  nohup ./gradlew ":services:${svc}:bootRun" > "logs/${svc}.log" 2>&1 &
  echo $! >> logs/pids.txt
done

echo "모든 서비스 기동 시작됨. 로그는 logs/*.log 에서 확인, PID는 logs/pids.txt"
echo "실시간으로 전체 로그 보려면: tail -f logs/*.log"
echo "전부 종료하려면: ./stop-services.sh"
