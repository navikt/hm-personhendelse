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
        languageVersion = JavaLanguageVersion.of(25)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        val test = named<JvmTestSuite>("test") {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(libs.hotlibs.test)
                implementation(libs.kafka.streams.test.utils)
            }
        }
    }
}

application { mainClass.set("no.nav.hjelpemidler.personhendelse.ApplicationKt") }
