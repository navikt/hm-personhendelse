package no.nav.hjelpemidler.personhendelse.kafka

import no.nav.hjelpemidler.personhendelse.Configuration
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology

fun topology(block: StreamsBuilder.() -> Unit): Topology = StreamsBuilder().apply(block).build()

fun kafkaStreams(
    configuration: Map<String, String> = Configuration.kafkaStreamsConfiguration(),
    block: StreamsBuilder.() -> Unit,
): KafkaStreams = KafkaStreams(topology(block), configuration.toProperties())
