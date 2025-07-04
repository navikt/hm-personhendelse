package no.nav.hjelpemidler.personhendelse.domain

import no.nav.hjelpemidler.kafka.KafkaMessage
import java.time.Instant

interface Event : KafkaMessage {
    val opprettet: Instant
}
