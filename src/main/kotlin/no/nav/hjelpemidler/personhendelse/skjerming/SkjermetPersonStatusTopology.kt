package no.nav.hjelpemidler.personhendelse.skjerming

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.toPersonIdent
import no.nav.hjelpemidler.personhendelse.Configuration
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
    .map { ident, skjermet -> ident.toPersonIdent() withValue skjermet.toBoolean() }
    .peek { ident, skjermet ->
        log.info { "Mottok melding om skjermet person" }
        secureLog.info { "Mottok melding om skjermet person, ident: $ident, skjermet: $skjermet" }
    }
    .filter { ident, skjermet ->
        val harFnr = ident is Fødselsnummer
        if (!harFnr) {
            secureLog.info { "Ignorerer ident: $ident, mangler fnr, skjermet: $skjermet" }
        }
        harFnr
    }
    .selectKey { ident, _ -> ident as Fødselsnummer }
    .mapValues(::SkjermetPersonStatusEvent)
    .toRapid()
