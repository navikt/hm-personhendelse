package no.nav.hjelpemidler.personhendelse.test

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.TestOutputTopic

fun <K, V> TestOutputTopic<K, V>.asSequence(): Sequence<KeyValue<K, V>> =
    generateSequence {
        when {
            isEmpty -> null
            else -> readKeyValue()
        }
    }
