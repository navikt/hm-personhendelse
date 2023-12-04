package no.nav.hjelpemidler.personhendelse.skjerming

import mu.KotlinLogging
import no.nav.hjelpemidler.personhendelse.domain.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.log.secureLog
import no.nav.hjelpemidler.personhendelse.processor.Processor

private val log = KotlinLogging.logger {}

val skjermedePersonerStatusProcessor: Processor<Fødselsnummer, Boolean, SkjermetPersonStatusEvent> =
    Processor { fnr, erSkjermet ->
        log.info { "Mottok melding om skjermet person" }
        secureLog.info { "Mottok melding om skjermet person, fnr: $fnr, erSkjermet: $erSkjermet" }
        SkjermetPersonStatusEvent(SkjermetPersonStatus(fnr, erSkjermet))
    }
