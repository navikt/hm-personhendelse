package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.domene.Event
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.Processor
import java.time.Instant
import java.util.UUID

val skjermetPersonStatusProcessor: Processor<Fødselsnummer, Boolean, SkjermetPersonStatusEvent> =
    Processor { fnr, erSkjermet ->
        SkjermetPersonStatusEvent(fnr, erSkjermet)
    }

data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val erSkjermet: Boolean
) : Event {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-skjermet-person-status"
    override val opprettet: Instant = Instant.now()
}
