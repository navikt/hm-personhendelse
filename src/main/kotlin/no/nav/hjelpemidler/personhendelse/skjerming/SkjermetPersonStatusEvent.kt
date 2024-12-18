package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domain.Event
import java.time.Instant
import java.util.UUID

data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val skjermet: Boolean,
    override val eventId: UUID = UUID.randomUUID(),
    override val opprettet: Instant = Instant.now(),
) : Event {
    override val eventName: String = "hm-personhendelse-skjermet-person-status"
}
