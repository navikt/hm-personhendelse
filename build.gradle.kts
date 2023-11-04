plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.spotless)
}

application { mainClass.set("no.nav.hjelpemidler.personhendelse.ApplicationKt") }

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.logging)
    implementation(libs.rapidsAndRivers)
    implementation(libs.kafka.clients)
    implementation(libs.kafka.streams)
    implementation(libs.kafka.avro.serializer)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.hm.http)

    runtimeOnly(libs.logback.classic)

    testImplementation(libs.bundles.test)
    testImplementation(libs.kafka.streams.test.utils)
    testImplementation(libs.testcontainers.kafka)
}

val javaVersion = JavaLanguageVersion.of(17)
java { toolchain { languageVersion.set(javaVersion) } }
kotlin { jvmToolchain { languageVersion.set(javaVersion) } }

tasks.test { useJUnitPlatform() }
