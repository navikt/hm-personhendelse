package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.domene.Event
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.PersonId
import no.nav.hjelpemidler.personhendelse.kafka.Processor
import no.nav.hjelpemidler.personhendelse.log.secureLog
import org.apache.kafka.streams.kstream.Predicate
import java.time.Instant
import java.util.UUID

val skjermetPersonStatusFilter: Predicate<PersonId, Boolean> = Predicate { personId, skjermet ->
    val fnr = personId is Fødselsnummer
    if (!fnr) {
        secureLog.info { "Ignorerer personId: $personId, ikke fnr, skjermet: $skjermet" }
    }
    fnr
}

val skjermetPersonStatusProcessor: Processor<Fødselsnummer, Boolean, SkjermetPersonStatusEvent> =
    Processor { fnr, skjermet ->
        SkjermetPersonStatusEvent(fnr, skjermet)
    }

data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val skjermet: Boolean
) : Event {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-skjermet-person-status"
    override val opprettet: Instant = Instant.now()
}
