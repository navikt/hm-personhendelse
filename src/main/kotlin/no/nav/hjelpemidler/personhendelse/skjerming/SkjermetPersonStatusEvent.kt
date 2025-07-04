package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.kafka.KafkaEvent
import no.nav.hjelpemidler.personhendelse.domain.Event
import java.time.Instant
import java.util.UUID

@KafkaEvent("hm-personhendelse-skjermet-person-status")
data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val skjermet: Boolean,
    override val eventId: UUID = UUID.randomUUID(),
    override val opprettet: Instant = Instant.now(),
) : Event
