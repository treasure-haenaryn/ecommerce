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
    implementation("org.springframework.kafka:spring-kafka")

    developmentOnly("me.paulschwarz:springboot4-dotenv")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.named<JavaExec>("bootRun") {
    workingDir = rootProject.projectDir
}
