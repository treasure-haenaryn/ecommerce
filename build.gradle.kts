plugins {
    java
    // apply false : 플러그인을 버전까지 다운로드. 루트 프로젝트에는 적용하지 않고 모듈에서 쓰기 위해 등록하는 과정
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "io.github.treasurehaenaryn.msa"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

// 각 서브모듈의 build.gradle.kts 에서 아래 플러그인을 개별 선언합니다:
//   id("io.spring.dependency-management")   -> BOM 버전 관리 (common 모듈 포함 전체)
//   id("org.springframework.boot")          -> 실행 가능한 서비스 모듈에만 적용
// 버전은 위 root plugins에서 지정했으므로 생략가능.
