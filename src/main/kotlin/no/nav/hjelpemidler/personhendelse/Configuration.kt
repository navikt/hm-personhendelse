package no.nav.hjelpemidler.personhendelse

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import no.nav.hjelpemidler.configuration.EnvironmentVariable
import no.nav.hjelpemidler.configuration.KafkaEnvironmentVariable
import no.nav.hjelpemidler.configuration.environmentVariable
import no.nav.hjelpemidler.kafka.createKafkaClientConfiguration
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.BuiltInDslStoreSuppliers.InMemoryDslStoreSuppliers
import java.util.Properties

object Configuration {
    val HTTP_PORT: Int by environmentVariable()
    val KAFKA_APPLICATION_ID by EnvironmentVariable
    val KAFKA_RAPID_TOPIC by EnvironmentVariable
    val LEESAH_TOPIC by EnvironmentVariable
    val SKJERMEDE_PERSONER_STATUS_TOPIC by EnvironmentVariable

    fun kafkaSchemaRegistryConfiguration(): Map<String, String> = mapOf(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY,
        AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE to "USER_INFO",
        SchemaRegistryClientConfig.USER_INFO_CONFIG to "${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_USER}:${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_PASSWORD}",
    )

    fun kafkaStreamsConfiguration(): Properties = createKafkaClientConfiguration {
        this[StreamsConfig.APPLICATION_ID_CONFIG] = KAFKA_APPLICATION_ID
        this[StreamsConfig.DSL_STORE_SUPPLIERS_CLASS_CONFIG] = InMemoryDslStoreSuppliers::class.java.name
    }
}
