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
}
