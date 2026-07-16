package io.github.treasurehaenaryn.msa.common.events;

import java.time.Instant;

/**
 * 모든 Kafka 이벤트가 공통으로 따르는 봉투(Envelope) 구조.
 *
 * @param eventId    이벤트 고유 ID
 * @param eventType  이벤트 종류 식별자
 * @param version    이벤트 스키마 버전
 * @param occurredAt 이벤트 발생 시각
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

    public static <T> EventEnvelope<T> of(String eventId, String eventType, T payload) {
        return new EventEnvelope<>(eventId, eventType, 1, Instant.now(), payload);
    }
}
