package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domain.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced

fun StreamsBuilder.skjermedePersonerStatus() {
    val stringSerde = Serdes.String()

    this
        .stream(
            Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
            Consumed.with(stringSerde, stringSerde)
        )
        .map { fnr, erSkjermet ->
            val event = skjermedePersonerStatusProcessor(fnr.toFødselsnummer(), erSkjermet.toBoolean())
            KeyValue.pair(fnr, event)
        }
        .to(Configuration.KAFKA_RAPID_TOPIC, Produced.with(stringSerde, jsonSerde()))
}
