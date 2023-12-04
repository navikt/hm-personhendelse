package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.event.Event
import java.time.Instant
import java.util.UUID

data class SkjermetPersonStatusEvent(
    override val data: SkjermetPersonStatus
) : Event<SkjermetPersonStatus> {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-skjermet-person-status"
    override val opprettet: Instant = Instant.now()
}
