package no.nav.hjelpemidler.personhendelse.skjerming

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.toPersonIdent
import no.nav.hjelpemidler.logging.teamInfo
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.streams.serialization.fødselsnummerSerde
import no.nav.hjelpemidler.streams.serialization.serde
import no.nav.hjelpemidler.streams.toRapid
import no.nav.hjelpemidler.streams.withValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

private val log = KotlinLogging.logger {}

fun StreamsBuilder.skjermetPersonStatus(): Unit = this
    .stream(
        Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
        Consumed.with(serde<String>(), serde<String>())
    )
    .map { ident, skjermet -> ident.toPersonIdent() withValue skjermet.toBoolean() }
    .peek { ident, skjermet ->
        log.info { "Mottok melding om skjermet person" }
        log.teamInfo { "Mottok melding om skjermet person, ident: $ident, skjermet: $skjermet" }
    }
    .filter { ident, skjermet ->
        val harFnr = ident is Fødselsnummer
        if (!harFnr) {
            log.teamInfo { "Ignorerer ident: $ident, mangler fnr, skjermet: $skjermet" }
        }
        harFnr
    }
    .selectKey { ident, _ -> ident as Fødselsnummer }
    .mapValues(::SkjermetPersonStatusEvent)
    .toRapid<Fødselsnummer, SkjermetPersonStatusEvent>(fødselsnummerSerde())
