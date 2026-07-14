# kafka-msa-ecommerce

MSA 기반 분산 서비스 설계 및 Kafka 실무 경험을 위한 토이 프로젝트입니다.
이커머스 주문 처리 흐름(주문 → 결제 → 재고 → 배송 → 알림)을 Kafka 이벤트 기반 Saga로 구현합니다.

## 기술 스택

- Java 25
- Spring Boot 4.1.0 (Spring Framework 7)
- Gradle (Kotlin DSL) 멀티모듈
- Spring Kafka
- Spring Data JPA (H2 for local dev / PostgreSQL for 운영)

## 모듈 구조

```
kafka-msa-ecommerce/
├── common/
│   ├── common-events/         # 서비스 간 공유 이벤트 스키마(record), 토픽 상수
│   └── common-kafka-config/   # Producer/Consumer 공통 설정 (@AutoConfiguration)
└── services/
    ├── order-service/         # 8081 - 주문 생성, Saga 시작점
    ├── payment-service/       # 8082 - 결제 처리(Mock PG)
    ├── inventory-service/     # 8083 - 재고 예약/차감, 보상 트랜잭션
    ├── shipping-service/      # 8084 - 배송 상태 관리
    └── notification-service/  # 8085 - 이벤트 구독 기반 알림 발송
```