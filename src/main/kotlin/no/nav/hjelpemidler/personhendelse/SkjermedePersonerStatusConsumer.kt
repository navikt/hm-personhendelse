package no.nav.hjelpemidler.personhendelse

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.nav.hjelpemidler.configuration.KafkaEnvironmentVariable
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.BooleanDeserializer
import org.apache.kafka.common.serialization.StringDeserializer

class SkjermedePersonerStatusConsumer private constructor(
    private val consumer: KafkaConsumer<String, Boolean>,
) : AutoCloseable by consumer {
    private val topics = setOf(Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC)

    constructor(
        bootstrapServers: String = KafkaEnvironmentVariable.KAFKA_BROKERS,
    ) : this(
        KafkaConsumer(
            Configuration.kafkaConsumerConfiguration(bootstrapServers),
            StringDeserializer(),
            BooleanDeserializer(),
        ),
    )

    suspend fun start(): Job = coroutineScope {
        launch {
            use {
                consumer.subscribe(topics)
                consumer.asFlow().collect {
                    secureLog.info("Mottok melding fra ${Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC}, key: ${it.key()}, value: ${it.value()}")
                }
            }
        }
    }
}
