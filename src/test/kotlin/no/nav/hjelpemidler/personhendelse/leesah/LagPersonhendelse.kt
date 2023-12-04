package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import java.time.Instant
import java.util.UUID

fun lagPersonhendelse(fnr: String = "12345678910", block: Personhendelse.() -> Unit = {}) = Personhendelse()
    .apply {
        hendelseId = UUID.randomUUID().toString()
        opplysningstype = "test"
        endringstype = Endringstype.OPPRETTET
        opprettet = Instant.now()
        master = "test"
        personidenter = listOf(fnr)
    }
    .apply(block)
