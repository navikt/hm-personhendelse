package no.nav.hjelpemidler.personhendelse

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
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
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed

fun main() {
    embeddedServer(Netty, Configuration.HTTP_PORT, module = Application::main).start()
}

fun Application.main() {
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = meterRegistry
    }

    install(KafkaStreamsPlugin) {
        topology {
            val identhendelseStream = stream(
                Configuration.IDENTHENDELSE_TOPIC,
                Consumed.with(Serdes.String(), GenericAvroSerde().apply {
                    configure(Configuration.kafkaSchemaRegistryConfiguration(), false)
                })
            )
            val skjermingshendelseStream = stream(
                Configuration.SKJERMINGSHENDELSE_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String())
            )

            identhendelseStream.foreach { key, value ->
            }

            skjermingshendelseStream.foreach { key, value ->
            }
        }
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
