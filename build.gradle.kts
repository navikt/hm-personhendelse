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

application {
    mainClass.set("no.nav.hjelpemidler.hotsak.river.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
