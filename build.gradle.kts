plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.kotlin.stdlib)

    // Kafka
    implementation(libs.kafka.streams)
    implementation(libs.kafka.streams.avro.serde)

    // Ktor
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.metrics.micrometer)

    // Metrics
    implementation(libs.micrometer.registry.prometheus)

    // Jackson
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.module.kotlin)

    // DigiHoT
    implementation(libs.hm.http)

    // Logging
    implementation(libs.kotlin.logging)
    runtimeOnly(libs.logback.classic)
    runtimeOnly(libs.logstash.logback.encoder)

    // Test
    testImplementation(libs.bundles.test)
    testImplementation(libs.kafka.streams.test.utils)
    testImplementation(libs.testcontainers.kafka)
}

val javaVersion = JavaLanguageVersion.of(17)
java { toolchain { languageVersion.set(javaVersion) } }
kotlin { jvmToolchain { languageVersion.set(javaVersion) } }

tasks.test { useJUnitPlatform() }

application { mainClass.set("no.nav.hjelpemidler.personhendelse.ApplicationKt") }
