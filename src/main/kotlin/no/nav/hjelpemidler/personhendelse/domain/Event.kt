package no.nav.hjelpemidler.personhendelse.domain

import java.time.Instant
import java.util.UUID

interface Event {
    val eventId: UUID
    val eventName: String
    val opprettet: Instant
}
