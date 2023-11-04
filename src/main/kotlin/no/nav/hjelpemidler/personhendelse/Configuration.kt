package no.nav.hjelpemidler.personhendelse

import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.EnvironmentVariable
import no.nav.hjelpemidler.configuration.KafkaEnvironmentVariable
import no.nav.hjelpemidler.configuration.LocalEnvironment
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import java.net.InetAddress

object Configuration {
    val KAFKA_RAPID_TOPIC by EnvironmentVariable
    val KAFKA_RESET_POLICY by EnvironmentVariable
    val KAFKA_CONSUMER_GROUP_ID by EnvironmentVariable

    val PERSONHENDELSE_TOPIC by EnvironmentVariable
    val SKJERMEDE_PERSONER_STATUS_TOPIC by EnvironmentVariable

    fun kafkaConsumerConfiguration(bootstrapServers: String = KafkaEnvironmentVariable.KAFKA_BROKERS): Map<String, String> {
        val instanceId = InetAddress.getLocalHost().hostName
        val consumer = mapOf(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to KAFKA_RESET_POLICY,
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.CLIENT_ID_CONFIG to "consumer-$instanceId",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            ConsumerConfig.GROUP_ID_CONFIG to KAFKA_CONSUMER_GROUP_ID,
            ConsumerConfig.GROUP_INSTANCE_ID_CONFIG to instanceId,
        )
        return consumer + when (Environment.current) {
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
    }
}
