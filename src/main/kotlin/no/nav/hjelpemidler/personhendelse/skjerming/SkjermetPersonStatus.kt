package no.nav.hjelpemidler.personhendelse.skjerming

import no.nav.hjelpemidler.personhendelse.domain.Fødselsnummer

data class SkjermetPersonStatus(
    val fnr: Fødselsnummer,
    val erSkjermet: Boolean,
)
