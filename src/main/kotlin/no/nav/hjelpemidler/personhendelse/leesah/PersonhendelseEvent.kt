package no.nav.hjelpemidler.personhendelse.leesah

import com.fasterxml.jackson.annotation.JsonTypeInfo
import no.nav.hjelpemidler.personhendelse.domene.Event
import no.nav.person.pdl.leesah.Endringstype

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed interface PersonhendelseEvent : Event {
    val hendelseId: String
    val tidligereHendelseId: String?
    val opplysningstype: String
    val endringstype: Endringstype
}
