package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.toPersonident
import no.nav.person.pdl.leesah.Personhendelse

val Personhendelse.fnr: Fødselsnummer
    get() = personidenter
        .map(String::toPersonident)
        .filterIsInstance<Fødselsnummer>()
        .first() // fixme -> burde vi hatt single() eller kan vi ha historiske, ulike verdier her?
