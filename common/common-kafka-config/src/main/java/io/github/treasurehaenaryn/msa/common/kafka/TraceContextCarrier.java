package io.github.treasurehaenaryn.msa.common.kafka;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * trace context를 문자열로 캡처/복원하고, Kafka 헤더에 직접 기록하는 유틸리티.
 */
public class TraceContextCarrier {

    private static final TextMapSetter<Headers> HEADER_SETTER =
            (headers, key, value) -> headers.add(key, value.getBytes(StandardCharsets.UTF_8));

    private static final TextMapGetter<Map<String, String>> MAP_GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier == null ? null : carrier.get(key);
        }
    };

    private static final TextMapSetter<Map<String, String>> MAP_SETTER = Map::put;

    private final OpenTelemetry openTelemetry;

    public TraceContextCarrier(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    /** 현재 활성 trace context를 W3C traceparent 문자열로 캡처. */
    public String capture() {
        Map<String, String> carrier = new HashMap<>();
        openTelemetry.getPropagators().getTextMapPropagator()
                .inject(Context.current(), carrier, MAP_SETTER);
        return carrier.get("traceparent");
    }

    /** traceparent 문자열을 부모 Context로 복원. */
    public Context extractParentContext(String traceparent) {
        if (traceparent == null || traceparent.isBlank()) {
            return Context.root();
        }
        Map<String, String> carrier = new HashMap<>();
        carrier.put("traceparent", traceparent);
        return openTelemetry.getPropagators().getTextMapPropagator()
                .extract(Context.root(), carrier, MAP_GETTER);
    }

    /** 수동 Span 생성용 Tracer. */
    public Tracer tracer(String instrumentationName) {
        return openTelemetry.getTracer(instrumentationName);
    }

    /** Context를 Kafka 메시지 헤더에 직접 기록. */
    public void injectContext(Context context, Headers headers) {
        openTelemetry.getPropagators().getTextMapPropagator().inject(context, headers, HEADER_SETTER);
    }
}
