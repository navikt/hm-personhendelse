package no.nav.hjelpemidler.personhendelse.skjerming

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.logging.teamInfo
import no.nav.hjelpemidler.logging.teamWarn
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.streams.serialization.fødselsnummerSerde
import no.nav.hjelpemidler.streams.serialization.serde
import no.nav.hjelpemidler.streams.toRapid
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

private val log = KotlinLogging.logger {}

fun StreamsBuilder.skjermetPersonStatus(): Unit = this
    .stream(
        Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
        Consumed.with(serde<String>(), serde<String>())
    )
    .mapValues(String::toBoolean)
    .filter { ident, skjermet ->
        val isFnr = Fødselsnummer.erGyldig(ident)
        if (!isFnr) {
            log.teamWarn { "Mottok melding om skjermet person uten gyldig fødselsnummer, ident: '$ident', skjermet: $skjermet" }
        }
        isFnr
    }
    .selectKey { ident, _ -> Fødselsnummer(ident) }
    .peek { ident, skjermet ->
        log.info { "Mottok melding om skjermet person" }
        if (Environment.current.isDev) {
            log.teamInfo { "Mottok melding om skjermet person, ident: '$ident', skjermet: $skjermet" }
        }
    }
    .mapValues(::SkjermetPersonStatusEvent)
    .toRapid<Fødselsnummer, SkjermetPersonStatusEvent>(fødselsnummerSerde())
