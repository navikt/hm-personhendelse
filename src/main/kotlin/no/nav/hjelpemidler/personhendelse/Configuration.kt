package no.nav.hjelpemidler.personhendelse

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.EnvironmentVariable
import no.nav.hjelpemidler.configuration.KafkaEnvironmentVariable
import no.nav.hjelpemidler.configuration.LocalEnvironment
import no.nav.hjelpemidler.configuration.environmentVariable
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig

object Configuration {
    val HTTP_PORT: Int by environmentVariable()
    val KAFKA_APPLICATION_ID by EnvironmentVariable
    val KAFKA_RAPID_TOPIC by EnvironmentVariable
    val IDENTHENDELSE_TOPIC by EnvironmentVariable
    val SKJERMINGSHENDELSE_TOPIC by EnvironmentVariable

    fun kafkaSecurityConfiguration(): Map<String, String> = when (Environment.current) {
        LocalEnvironment -> mapOf(
            CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to SecurityProtocol.PLAINTEXT.name,

            SaslConfigs.SASL_MECHANISM to "PLAIN",
        )

        else -> mapOf(
            CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to SecurityProtocol.SSL.name,

            SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG to "",
            SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to KafkaEnvironmentVariable.KAFKA_KEYSTORE_PATH,
            SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to KafkaEnvironmentVariable.KAFKA_CREDSTORE_PASSWORD,
            SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to "PKCS12",
            SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to KafkaEnvironmentVariable.KAFKA_TRUSTSTORE_PATH,
            SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to KafkaEnvironmentVariable.KAFKA_CREDSTORE_PASSWORD,
            SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG to "jks",
        )
    }

    fun kafkaSchemaRegistryConfiguration(): Map<String, String> = mapOf(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY,
        SchemaRegistryClientConfig.USER_INFO_CONFIG to "${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_USER}:${KafkaEnvironmentVariable.KAFKA_SCHEMA_REGISTRY_PASSWORD}",
    )

    fun kafkaStreamsConfiguration(bootstrapServers: String = KafkaEnvironmentVariable.KAFKA_BROKERS): Map<String, String> =
        mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to KAFKA_APPLICATION_ID,
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
        ) + kafkaSecurityConfiguration()
}
