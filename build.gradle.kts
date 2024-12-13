plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.spotless)
}

dependencies {
    // Kafka
    implementation(libs.kafka.streams)
    implementation(libs.kafka.streams.avro.serde)
    constraints {
        implementation(libs.commons.compress)
    }

    // Ktor
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.metrics.micrometer)

    // Metrics
    implementation(libs.micrometer.registry.prometheus)

    // Jackson
    implementation(libs.bundles.jackson)

    // DigiHoT
    implementation(libs.hotlibs.core)
    implementation(libs.hotlibs.kafka)
    implementation(libs.hm.contract.pdl.avro)

    // Logging
    implementation(libs.kotlin.logging)
    runtimeOnly(libs.bundles.logging.runtime)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

@Suppress("UnstableApiUsage")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest(libs.versions.kotlin.asProvider())
            dependencies {
                implementation(libs.kotest.assertions.core)
                implementation(libs.kafka.streams.test.utils)
            }
        }
    }
}

application { mainClass.set("no.nav.hjelpemidler.personhendelse.ApplicationKt") }
