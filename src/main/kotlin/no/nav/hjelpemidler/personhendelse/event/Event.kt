package no.nav.hjelpemidler.personhendelse.event

import java.time.Instant
import java.util.UUID

interface Event<out T : Any> {
    val eventId: UUID
    val eventName: String
    val opprettet: Instant
    val data: T
}
