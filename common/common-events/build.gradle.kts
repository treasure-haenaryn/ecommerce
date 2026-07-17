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
    // jackson-annotations만 예외적으로 com.fasterxml.jackson 그룹에 그대로 남아있음 (2.x/3.x 공용)
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("tools.jackson.core:jackson-databind")
}
