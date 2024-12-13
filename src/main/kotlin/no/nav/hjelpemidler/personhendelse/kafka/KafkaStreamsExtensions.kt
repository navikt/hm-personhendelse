package no.nav.hjelpemidler.personhendelse.kafka

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.Configuration
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import java.util.Properties

fun topology(block: StreamsBuilder.() -> Unit): Topology = StreamsBuilder().apply(block).build()

fun kafkaStreams(
    configuration: Properties = Configuration.kafkaStreamsConfiguration(),
    block: StreamsBuilder.() -> Unit,
): KafkaStreams = KafkaStreams(topology(block), configuration)

infix fun <K, V> K.withValue(value: V): KeyValue<K, V> = KeyValue.pair(this, value)

inline fun <reified T> KStream<Fødselsnummer, T>.toRapid() {
    to(Configuration.KAFKA_RAPID_TOPIC, Produced.with(fødselsnummerSerde, jsonSerde<T>()))
}
