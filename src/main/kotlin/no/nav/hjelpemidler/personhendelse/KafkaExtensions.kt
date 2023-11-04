package no.nav.hjelpemidler.personhendelse

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

fun <K, V> KafkaConsumer<K, V>.asFlow(timeout: Duration = 1.seconds): Flow<ConsumerRecord<K, V>> =
    flow {
        poll(timeout.toJavaDuration()).forEach { emit(it) }
    }
