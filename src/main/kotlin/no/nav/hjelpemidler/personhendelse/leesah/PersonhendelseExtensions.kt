package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.toPersonIdent
import no.nav.person.pdl.leesah.Personhendelse

val Personhendelse.harFnr: Boolean
    get() = personidenter
        .map(String::toPersonIdent)
        .filterIsInstance<Fødselsnummer>()
        .isNotEmpty()

val Personhendelse.fnr: Fødselsnummer
    get() = personidenter
        .map(String::toPersonIdent)
        .filterIsInstance<Fødselsnummer>()
        .first() // fixme -> burde vi hatt single() eller kan vi ha historiske, ulike verdier her?

val Personhendelse.kilde: PersonhendelseEvent.Kilde
    get() = PersonhendelseEvent.Kilde(this)

val Personhendelse.sammendrag: String
    get() = mapOf(
        "hendelseId" to hendelseId,
        "tidligereHendelseId" to tidligereHendelseId,
        "opprettet" to opprettet,
        "opplysningstype" to opplysningstype,
        "endringstype" to endringstype,
        "master" to master,
    ).map { (key, value) -> "$key: $value" }.joinToString()

var Personhendelse.behandletOpplysningstype: BehandletOpplysningstype?
    get() = behandletOpplysningstypeOf(opplysningstype)
    set(value) {
        opplysningstype = value?.opplysningstype
    }
