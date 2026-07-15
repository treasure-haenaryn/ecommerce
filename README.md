# kafka-msa-ecommerce

MSA 기반 분산 서비스 설계 및 Kafka 실무 경험을 위한 토이 프로젝트입니다.
이커머스 주문 처리 흐름(주문 → 결제 → 재고 → 배송 → 알림)을 Kafka 이벤트 기반 Saga로 구현합니다.

## 기술 스택

- Java 25
- Spring Boot 4.1.0 (Spring Framework 7)
- Gradle (Kotlin DSL) 멀티모듈
- Spring Kafka
- Spring Data JPA + PostgreSQL

## 모듈 구조

```
kafka-msa-ecommerce/
├── docker-compose.yml          # Kafka(KRaft) + Kafka UI + PostgreSQL
├── docker/postgres-init.sql    # 서비스별 DB 5개 생성 스크립트
├── .env / .env.example         # DB 계정 등 민감 정보 (.env는 git 제외)
├── common/
│   ├── common-events/          # EventEnvelope<T> + 이벤트별 payload record + 토픽 상수
│   └── common-kafka-config/    # Producer/Consumer 공통 설정 (@AutoConfiguration)
└── services/
    ├── order-service/          # 8081 - 주문 생성, Saga 시작점
    ├── payment-service/        # 8082 - 결제 처리(Mock PG)
    ├── inventory-service/      # 8083 - 재고 예약/차감, 보상 트랜잭션
    ├── shipping-service/       # 8084 - 배송 상태 관리
    └── notification-service/   # 8085 - 이벤트 구독 기반 알림 발송
```

각 서비스는 `domain / application / infrastructure(web, messaging)` 패키지로 레이어를 구분해 두었습니다.
(Phase 2에서 실제 도메인 로직을 채워 넣습니다. 현재는 뼈대만 존재합니다.)

## 이벤트 흐름

```
OrderCreated
  → PaymentCompleted | PaymentFailed
  → InventoryReserved | InventoryReservationFailed
  → ShippingStarted
  → OrderCompleted
```

- 파티션 키: `orderId` (같은 주문의 이벤트는 순서 보장)
- 실패 이벤트(`PaymentFailed`, `InventoryReservationFailed`)는 보상 트랜잭션의 트리거

## 환경변수 설정 (.env)

DB 계정 정보는 `.env` 파일로 관리하며, `.env`는 git에 커밋하지 않습니다. 최초 1회 아래처럼 준비하세요.

```bash
cp .env.example .env
# .env 안의 DB_USERNAME, DB_PASSWORD 값을 실제로 쓸 값으로 채우기
```

- `docker-compose.yml`은 `.env`를 자동으로 읽어서 Postgres 계정을 설정합니다.
- 각 서비스(Spring Boot 앱)는 `me.paulschwarz:springboot4-dotenv` 라이브러리를 통해 `./gradlew bootRun` 실행 시 루트 `.env`를 자동으로 읽습니다. 별도 `export` 없이 바로 실행하면 됩니다.

## 로컬 인프라 실행 (Docker Compose)

```bash
docker compose up -d
```

기동되는 것:
- **Kafka** (KRaft 모드, 1-broker) — `localhost:9092`
- **Kafka UI** — http://localhost:8090 (토픽/컨슈머 그룹/메시지 확인용)
- **PostgreSQL** — `localhost:5432`, 서비스별 DB 5개(`order_db`, `payment_db`, `inventory_db`, `shipping_db`, `notification_db`) 자동 생성 (계정은 `.env`의 `DB_USERNAME` / `DB_PASSWORD`)

종료:
```bash
docker compose down        # 컨테이너만 정리 (DB 데이터는 볼륨에 유지)
docker compose down -v     # 볼륨까지 완전 초기화
```

## 빌드 / 실행

```bash
./gradlew clean build
./gradlew :services:order-service:bootRun
```

## 참고

- `common-kafka-config`는 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`를 통해 각 서비스에 자동 적용됩니다. 별도 `@Import`나 `@ComponentScan` 설정이 필요 없습니다.
- Kafka Consumer는 기본적으로 역직렬화 실패/처리 실패 시 1초 간격 3회 재시도 후 `{topic}.DLT` 로 전송하도록 설정되어 있습니다.
- Producer는 `enable.idempotence=true`, `acks=all`로 설정되어 있어 중복 없는 전송을 보장합니다.

## 진행 상황

전체 로드맵은 Obsidian `Career Vault/MSA/01. MSA-Kafka 토이프로젝트 작업계획.md` 참고.

- [x] Phase 0 — 멀티모듈 프로젝트 구조 생성
- [ ] Phase 1 — 로컬 개발 환경 최소 구성 (Docker Compose)
- [ ] Phase 2 — 도메인 로직 개발
- [ ] Phase 3 — 통합 테스트 & 안정화
- [ ] Phase 4 — 컨테이너화
- [ ] Phase 5 — K8s 이관
- [ ] Phase 6 — 운영 요소 & 관측성
