package no.nav.hjelpemidler.personhendelse.leesah

import com.fasterxml.jackson.annotation.JsonTypeInfo
import no.nav.hjelpemidler.kafka.KafkaMessage
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import java.time.Instant

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed interface PersonhendelseEvent : KafkaMessage {
    val kilde: Kilde

    data class Kilde(
        val hendelseId: String,
        val tidligereHendelseId: String?,
        val opprettet: Instant,
        val opplysningstype: String,
        val endringstype: Endringstype,
        val master: String,
    ) {
        constructor(personhendelse: Personhendelse) : this(
            personhendelse.hendelseId,
            personhendelse.tidligereHendelseId,
            personhendelse.opprettet,
            personhendelse.opplysningstype,
            personhendelse.endringstype,
            personhendelse.master,
        )
    }
}
