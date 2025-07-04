package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.kafka.KafkaEvent
import no.nav.hjelpemidler.kafka.KafkaMessage
import java.time.Instant
import java.util.UUID

@KafkaEvent("hm-personhendelse-skjermet-person-status")
data class SkjermetPersonStatusEvent(
    val fnr: Fødselsnummer,
    val skjermet: Boolean,
    val opprettet: Instant = Instant.now(),
    override val eventId: UUID = UUID.randomUUID(),
) : KafkaMessage
