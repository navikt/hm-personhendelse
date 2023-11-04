package no.nav.hjelpemidler.personhendelse

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.nav.hjelpemidler.configuration.KafkaEnvironmentVariable
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer

class PersonhendelseConsumer private constructor(
    private val consumer: KafkaConsumer<String, GenericRecord>,
) : AutoCloseable by consumer {
    private val topics = setOf(Configuration.PERSONHENDELSE_TOPIC)

    constructor(
        bootstrapServers: String = KafkaEnvironmentVariable.KAFKA_BROKERS,
    ) : this(
        KafkaConsumer<String, GenericRecord>(
            Configuration.kafkaConsumerConfiguration(bootstrapServers) +
                    mapOf(
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.qualifiedName,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.qualifiedName,
                        KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY,
                        KafkaAvroDeserializerConfig.USER_INFO_CONFIG to "${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_USER}:${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_PASSWORD}",
                    ),
        ),
    )

    suspend fun start(): Job = coroutineScope {
        launch {
            consumer.subscribe(topics)
        }
    }
}
