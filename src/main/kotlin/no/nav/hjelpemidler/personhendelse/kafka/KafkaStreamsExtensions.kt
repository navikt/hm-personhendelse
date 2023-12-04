package no.nav.hjelpemidler.personhendelse.kafka

import no.nav.hjelpemidler.personhendelse.Configuration
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Predicate

fun topology(block: StreamsBuilder.() -> Unit): Topology = StreamsBuilder().apply(block).build()

fun kafkaStreams(
    configuration: Map<String, String> = Configuration.kafkaStreamsConfiguration(),
    block: StreamsBuilder.() -> Unit,
): KafkaStreams = KafkaStreams(topology(block), configuration.toProperties())

operator fun <K, V> Predicate<in K, in V>.invoke(key: K, value: V): Boolean = test(key, value)

infix fun <K, V> Predicate<in K, in V>.and(other: Predicate<in K, in V>): Predicate<K, V> =
    Predicate { key, value -> this(key, value) && other(key, value) }

fun <K, V> Predicate<in K, in V>.negate(): Predicate<K, V> =
    Predicate { key, value -> !this(key, value) }

infix fun <K, V> Predicate<in K, in V>.or(other: Predicate<in K, in V>): Predicate<K, V> =
    Predicate { key, value -> this(key, value) || other(key, value) }

fun <K, V> not(target: Predicate<in K, in V>): Predicate<in K, in V> = target.negate()

fun <K, V> Collection<Predicate<K, V>>.any(): Predicate<K, V> = reduce(Predicate<K, V>::or)

fun <K, V> Collection<Predicate<K, V>>.all(): Predicate<K, V> = reduce(Predicate<K, V>::and)
