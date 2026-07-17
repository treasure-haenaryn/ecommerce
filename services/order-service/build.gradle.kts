plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("me.paulschwarz:spring-dotenv-bom:5.1.0")
    }
}

dependencies {
    implementation(project(":common:common-events"))
    implementation(project(":common:common-kafka-config"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.kafka:spring-kafka")

    // 로컬 실행 시 루트 .env 파일을 자동으로 읽어 Spring Environment에 주입 (운영 배포 시에는 실제 환경변수/Secret이 우선 적용됨)
    developmentOnly("me.paulschwarz:springboot4-dotenv")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.named<JavaExec>("bootRun") {
    // .env 파일이 프로젝트 루트에 있으므로, 루트를 작업 디렉토리로 지정해 dotenv가 찾을 수 있게 함
    workingDir = rootProject.projectDir
}
