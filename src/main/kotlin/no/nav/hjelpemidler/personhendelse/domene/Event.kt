package no.nav.hjelpemidler.personhendelse.domene

import java.time.Instant
import java.util.UUID

interface Event {
    val eventId: UUID
    val eventName: String
    val opprettet: Instant
}
