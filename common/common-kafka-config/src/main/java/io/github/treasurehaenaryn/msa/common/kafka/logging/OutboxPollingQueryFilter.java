package io.github.treasurehaenaryn.msa.common.kafka.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Outbox Poller가 1초마다 날리는 "미발행 이벤트 조회" 쿼리 로그만 콘솔에서 제외하는 필터.
 * 다른 SQL(주문 저장, 재고 차감 등 실제 비즈니스 쿼리)은 그대로 다 통과시킨다.
 */
public class OutboxPollingQueryFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message != null
                && message.contains("from outbox_event")
                && message.contains("published=false")) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
