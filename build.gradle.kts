plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.logging)
    implementation(libs.rapidsAndRivers)
    implementation(libs.hm.http)

    testImplementation(libs.bundles.test)
}

val javaVersion = JavaLanguageVersion.of(17)
java { toolchain { languageVersion.set(javaVersion) } }
kotlin { jvmToolchain { languageVersion.set(javaVersion) } }

application { mainClass.set("no.nav.hjelpemidler.personhendelse.ApplicationKt") }

tasks.test { useJUnitPlatform() }
