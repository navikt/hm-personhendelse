package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.lagFødselsnummer
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import java.time.Instant
import java.util.UUID

fun lagPersonhendelse(
    fnr: Fødselsnummer = lagFødselsnummer(30),
    block: Personhendelse.() -> Unit = {}
): Personhendelse = Personhendelse()
    .apply {
        hendelseId = UUID.randomUUID().toString()
        opplysningstype = "test"
        endringstype = Endringstype.OPPRETTET
        opprettet = Instant.now()
        master = "test"
        personidenter = listOf(fnr.toString())
    }
    .apply(block)
