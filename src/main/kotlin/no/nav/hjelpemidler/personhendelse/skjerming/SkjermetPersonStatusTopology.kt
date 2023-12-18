package no.nav.hjelpemidler.personhendelse.skjerming

import mu.KotlinLogging
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.GcpEnvironment
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.toPersonId
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.toIf
import no.nav.hjelpemidler.personhendelse.kafka.withValue
import no.nav.hjelpemidler.personhendelse.log.secureLog
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced

private val log = KotlinLogging.logger {}

fun StreamsBuilder.skjermetPersonStatus() {
    val stringSerde = Serdes.String()
    val skjermetPersonStatusEventSerde = jsonSerde<SkjermetPersonStatusEvent>()

    this
        .stream(
            Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
            Consumed.with(stringSerde, stringSerde)
        )
        .map { personId, skjermet ->
            personId.toPersonId() withValue skjermet.toBoolean()
        }
        .peek { personId, skjermet ->
            log.info { "Mottok melding om skjermet person" }
            secureLog.info { "Mottok melding om skjermet person, personId: $personId, skjermet: $skjermet" }
        }
        .filter(skjermetPersonStatusFilter)
        .selectKey { personId, _ -> personId as Fødselsnummer }
        .map { fnr, skjermet ->
            val event = skjermetPersonStatusProcessor(fnr, skjermet)
            fnr.toString() withValue event
        }
        .toIf(
            Environment.current != GcpEnvironment.PROD,
            Configuration.KAFKA_RAPID_TOPIC,
            Produced.with(stringSerde, skjermetPersonStatusEventSerde),
        )
}
