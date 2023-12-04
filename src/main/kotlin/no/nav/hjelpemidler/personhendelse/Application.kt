package no.nav.hjelpemidler.personhendelse

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.hjelpemidler.personhendelse.kafka.KafkaStreamsPlugin
import no.nav.hjelpemidler.personhendelse.kafka.kafkaStreams
import no.nav.hjelpemidler.personhendelse.leesah.personhendelse
import no.nav.hjelpemidler.personhendelse.skjerming.skjermetPersonStatus

fun main() {
    embeddedServer(Netty, Configuration.HTTP_PORT, module = Application::main).start()
}

fun Application.main() {
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = meterRegistry
    }

    val kafkaStreams = kafkaStreams {
        personhendelse()
        skjermetPersonStatus()
    }
    install(KafkaStreamsPlugin) {
        this.kafkaStreams = kafkaStreams
    }

    routing {
        get("/isalive") {
            call.respond(HttpStatusCode.OK)
        }
        get("/isready") {
            call.respond(HttpStatusCode.OK)
        }
        get("/metrics") {
            call.respond(meterRegistry.scrape())
        }
    }
}
