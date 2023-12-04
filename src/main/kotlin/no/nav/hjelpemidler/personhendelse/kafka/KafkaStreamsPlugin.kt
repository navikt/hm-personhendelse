package no.nav.hjelpemidler.personhendelse.kafka

import io.ktor.events.EventDefinition
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import mu.KotlinLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KafkaStreams.State

private val log = KotlinLogging.logger {}

class KafkaStreamsPluginConfiguration {
    lateinit var kafkaStreams: KafkaStreams
}

val KafkaStreamsPlugin = createApplicationPlugin("KafkaStreamsPlugin", ::KafkaStreamsPluginConfiguration) {
    val kafkaStreams = pluginConfig.kafkaStreams

    kafkaStreams.setStateListener { newState, oldState ->
        application.environment.monitor.raise(
            KafkaStreamsStateTransitionEvent,
            KafkaStreamsStateTransition(newState, oldState),
        )
    }

    on(MonitoringEvent(ApplicationStarted)) { _ ->
        kafkaStreams.cleanUp()
        kafkaStreams.start()
        log.info { "KafkaStreams startet" }
    }

    on(MonitoringEvent(ApplicationStopped)) { _ ->
        kafkaStreams.close()
        log.info { "KafkaStreams stoppet" }
        application.environment.monitor.unsubscribe(ApplicationStarted) {}
        application.environment.monitor.unsubscribe(ApplicationStopped) {}
    }

    onCall { _ ->
        val state = kafkaStreams.state()
        log.info { "state: $state" }
    }
}

data class KafkaStreamsStateTransition(val newState: State, val oldState: State)

val KafkaStreamsStateTransitionEvent: EventDefinition<KafkaStreamsStateTransition> = EventDefinition()
