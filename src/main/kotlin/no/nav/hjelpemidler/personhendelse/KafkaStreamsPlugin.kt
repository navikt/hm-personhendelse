package no.nav.hjelpemidler.personhendelse

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import mu.KotlinLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder

private val log = KotlinLogging.logger {}

class KafkaStreamsPluginConfiguration internal constructor() {
    internal val builder = StreamsBuilder()

    fun topology(block: StreamsBuilder.() -> Unit) = builder.block()
}

val KafkaStreamsPlugin = createApplicationPlugin("KafkaStreamsPlugin", ::KafkaStreamsPluginConfiguration) {
    val streams = KafkaStreams(pluginConfig.builder.build(), Configuration.kafkaStreamsConfiguration().toProperties())

    on(MonitoringEvent(ApplicationStarted)) {
        streams.cleanUp()
        streams.start()
        log.info { "KafkaStreams startet" }
    }

    on(MonitoringEvent(ApplicationStopped)) {
        streams.close()
        log.info { "KafkaStreams stoppet" }
    }
}
