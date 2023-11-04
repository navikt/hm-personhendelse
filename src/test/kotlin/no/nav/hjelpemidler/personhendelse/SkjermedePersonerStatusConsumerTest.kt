package no.nav.hjelpemidler.personhendelse

import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.BooleanSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class SkjermedePersonerStatusConsumerTest {
    @Test
    fun `Gjør noe smart her`() {
        val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))
        container.start()

        val client = AdminClient.create(
            mapOf(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers,
            )
        )

        client.createTopics(
            setOf(
                NewTopic(Configuration.KAFKA_RAPID_TOPIC, 1, 1),
                NewTopic(Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC, 1, 1),
            )
        )

        val producer = KafkaProducer(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers,
            ),
            StringSerializer(),
            BooleanSerializer(),
        )
        producer.send(ProducerRecord(Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC, "test", true))

        val consumer = SkjermedePersonerStatusConsumer(container.bootstrapServers)
        runBlocking { consumer.start() }
    }
}
