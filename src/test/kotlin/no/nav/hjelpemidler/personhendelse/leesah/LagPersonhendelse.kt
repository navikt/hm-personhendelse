package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.år
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import java.time.Instant
import java.util.UUID

fun lagPersonhendelse(
    fnr: Fødselsnummer = Fødselsnummer(30.år),
    block: Personhendelse.() -> Unit = {},
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
