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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

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
