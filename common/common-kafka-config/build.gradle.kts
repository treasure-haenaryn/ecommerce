plugins {
    `java-library`
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.0")
    }
}

dependencies {
    api(project(":common:common-events"))
    api("org.springframework.kafka:spring-kafka")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("tools.jackson.core:jackson-databind")
    api("io.opentelemetry:opentelemetry-api")
    api("io.opentelemetry:opentelemetry-context")

    // logback-spring.xml의 커스텀 필터(OutboxPollingQueryFilter)가 사용
    api("ch.qos.logback:logback-classic")
}
