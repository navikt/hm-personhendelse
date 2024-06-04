package no.nav.hjelpemidler.personhendelse.skjerming

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.toPersonId
import no.nav.hjelpemidler.personhendelse.kafka.stringSerde
import no.nav.hjelpemidler.personhendelse.kafka.toRapid
import no.nav.hjelpemidler.personhendelse.kafka.withValue
import no.nav.hjelpemidler.personhendelse.log.secureLog
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

private val log = KotlinLogging.logger {}

fun StreamsBuilder.skjermetPersonStatus(): Unit = this
    .stream(
        Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
        Consumed.with(stringSerde, stringSerde)
    )
    .map { personId, skjermet -> personId.toPersonId() withValue skjermet.toBoolean() }
    .peek { personId, skjermet ->
        log.info { "Mottok melding om skjermet person" }
        secureLog.info { "Mottok melding om skjermet person, personId: $personId, skjermet: $skjermet" }
    }
    .filter { personId, skjermet ->
        val harFnr = personId is Fødselsnummer
        if (!harFnr) {
            secureLog.info { "Ignorerer personId: $personId, mangler fnr, skjermet: $skjermet" }
        }
        harFnr
    }
    .selectKey { personId, _ -> personId as Fødselsnummer }
    .mapValues(::SkjermetPersonStatusEvent)
    .toRapid()
