package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced

fun StreamsBuilder.leesah() {
    val stringSerde = Serdes.String()

    this
        .stream(
            Configuration.LEESAH_TOPIC,
            Consumed.with(stringSerde, specificAvroSerde<Personhendelse>())
        )
        .flatMap { ident, personhendelse ->
            listOf(KeyValue.pair(ident, personhendelse))
        }
        .to(Configuration.KAFKA_RAPID_TOPIC, Produced.with(stringSerde, jsonSerde()))
}
