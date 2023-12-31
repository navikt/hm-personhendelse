rootProject.name = "hm-personhendelse"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
        maven("https://jitpack.io")
        maven {
            url = uri("https://maven.pkg.github.com/navikt/hm-contract-pdl-avro")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/navikt/hm-http")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/navikt/hm-katalog")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
        }
    }
    versionCatalogs {
        create("libs") {
            from("no.nav.hjelpemidler:hm-katalog:0.1.9")
            version(
                "confluent",
                "7.5.1"
            ) // pga. https://nav-it.slack.com/archives/C84H68ESC/p1700733466530099?thread_ts=1700556875.004519&cid=C84H68ESC
        }
    }
}
