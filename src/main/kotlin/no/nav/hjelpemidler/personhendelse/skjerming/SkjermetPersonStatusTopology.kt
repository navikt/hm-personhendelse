package no.nav.hjelpemidler.personhendelse.skjerming

import mu.KotlinLogging
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.GcpEnvironment
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.log.secureLog
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced

private val log = KotlinLogging.logger {}

fun StreamsBuilder.skjermetPersonStatus() {
    val stringSerde = Serdes.String()
    val skjermetPersonStatusEventSerde = jsonSerde<SkjermetPersonStatusEvent>()

    val stream = this
        .stream(
            Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
            Consumed.with(stringSerde, stringSerde)
        )
        .peek { fnr, erSkjermet ->
            log.info { "Mottok melding om skjermet person" }
            secureLog.info { "Mottok melding om skjermet person, fnr: $fnr, erSkjermet: $erSkjermet" }
        }
        .map { fnr, erSkjermet ->
            val event = skjermetPersonStatusProcessor(fnr.toFødselsnummer(), erSkjermet.toBoolean())
            KeyValue.pair(fnr, event)
        }

    if (Environment.current != GcpEnvironment.PROD) {
        stream
            .to(
                Configuration.KAFKA_RAPID_TOPIC,
                Produced.with(stringSerde, skjermetPersonStatusEventSerde),
            )
    }
}
