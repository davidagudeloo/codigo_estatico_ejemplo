plugins {
    java
    application
    jacoco
    id("org.sonarqube") version "7.5.0.8588"
}

group = "com.events.billing"
version = "1.0.0"

dependencyLocking {
    lockAllConfigurations()
}

sonar {
    properties {
        property("sonar.projectKey", "davidagudeloo_codigo_estatico_ejemplo")
        property("sonar.organization", "davidagudeloo")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

java {
    // Java 17 (LTS). El toolchain hace que Gradle use exactamente esta version.
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}



application {
    mainClass = "com.events.billing.Main"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit 5 (agregador: API + motor + parametros). Scope de test.
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")

    // Necesario en las versiones recientes de Gradle para lanzar la Plataforma JUnit.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    // Activa la Plataforma JUnit 5 (sin esto Gradle no encuentra los tests).
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        // Muestra en consola los println de los performance tests.
        showStandardStreams = true
    }

    // Genera el reporte de cobertura (jacocoTestReport) justo despues de correr los tests.
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named("sonar") {
    // El analisis necesita el XML de cobertura ya generado antes de correr.
    dependsOn(tasks.jacocoTestReport)
}
