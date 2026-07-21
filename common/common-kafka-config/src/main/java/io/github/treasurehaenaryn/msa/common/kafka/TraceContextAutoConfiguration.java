package io.github.treasurehaenaryn.msa.common.kafka;

import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * TraceContextCarrier 빈 등록.
 */
@AutoConfiguration
@ConditionalOnClass(OpenTelemetry.class)
public class TraceContextAutoConfiguration {

    @Bean
    public TraceContextCarrier traceContextCarrier(OpenTelemetry openTelemetry) {
        return new TraceContextCarrier(openTelemetry);
    }
}
