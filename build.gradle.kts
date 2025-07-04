plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.spotless)
}

dependencies {
    // hotlibs
    implementation(platform(libs.hotlibs.platform))
    implementation(libs.hotlibs.streams)
    implementation(libs.hm.contract.pdl.avro)
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
