package com.example.msakafka.common.events;

import java.time.Instant;

/**
 * 모든 Kafka 이벤트가 공통으로 따르는 봉투(Envelope) 구조.
 *
 * 설계 의도(2026-07-14 결정):
 * - 비즈니스 데이터(payload)와 메타데이터를 분리해서, 나중에 traceId 같은 필드가
 *   추가되어도 이 클래스 하나만 고치면 되도록 함 (개별 이벤트 레코드를 전부 건드릴 필요 없음)
 * - eventId는 멱등성 dedup 테이블의 PK로 사용 예정. Phase 2에서 Outbox 패턴을 붙일 때
 *   Outbox 테이블의 PK를 그대로 재사용하는 방식으로 "재전송해도 같은 eventId"가 되도록 할 계획.
 * - version은 향후 Avro/Protobuf + Schema Registry 전환(Phase 2~3 이후) 시
 *   스키마 호환성 검증의 기준점으로 사용.
 *
 * @param eventId    멱등성 처리를 위한 고유 이벤트 ID (Outbox PK 기반 예정)
 * @param eventType  이벤트 종류 식별자 (예: "OrderCreated") — 라우팅/로깅/역직렬화 판단에 사용
 * @param version    이벤트 스키마 버전 (스키마 진화 대비, 기본 1)
 * @param occurredAt 이벤트 발생 시각 (UTC 기준 Instant로 통일)
 * @param payload    실제 비즈니스 데이터
 * @param <T>        payload 타입
 */
public record EventEnvelope<T>(
        String eventId,
        String eventType,
        int version,
        Instant occurredAt,
        T payload
) {

    /**
     * version=1, occurredAt=now 로 봉투를 생성하는 편의 팩토리 메서드.
     */
    public static <T> EventEnvelope<T> of(String eventId, String eventType, T payload) {
        return new EventEnvelope<>(eventId, eventType, 1, Instant.now(), payload);
    }
}
