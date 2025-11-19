import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    jacoco
    checkstyle
    alias(libs.plugins.benManes)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.shadow)
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
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.hikari)
    implementation(libs.slf4jSimple)
    implementation(libs.jte)
    implementation(libs.javalin)
    implementation(libs.javalinBundle)
    implementation(libs.javalinRendering)
    implementation(libs.unirest)
    implementation(libs.jsoup)

    testImplementation(libs.assertjCore)
    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testImplementation(libs.mockWebserver)
    testImplementation(libs.okhttp)
    
    testRuntimeOnly(libs.junitPlatformLauncher)
    testRuntimeOnly(libs.junitJupiterEngine)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
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