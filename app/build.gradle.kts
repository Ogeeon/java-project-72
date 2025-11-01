import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    java
    jacoco
    id("com.github.ben-manes.versions") version "0.50.0"
    id("org.sonarqube") version "7.0.1.6134"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("hexlet.code.App")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.h2database:h2:2.4.240")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("gg.jte:jte:3.2.0")
    implementation("io.javalin:javalin:6.6.0")
    implementation("io.javalin:javalin-bundle:6.6.0")
    implementation("io.javalin:javalin-rendering:6.6.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }

sonar {
  properties {
    property("sonar.projectKey", "Ogeeon_java-project-72")
    property("sonar.organization", "ogeeon")
  }
}