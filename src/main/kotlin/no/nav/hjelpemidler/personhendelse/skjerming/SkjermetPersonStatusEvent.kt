package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.domene.Event
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import java.time.Instant
import java.util.UUID

data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val skjermet: Boolean
) : Event {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-skjermet-person-status"
    override val opprettet: Instant = Instant.now()
}
