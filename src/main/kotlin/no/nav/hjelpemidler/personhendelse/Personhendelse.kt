package no.nav.hjelpemidler.personhendelse

sealed interface Personhendelse {
    val fnr: String
}

data class Identhendelse(override val fnr: String) : Personhendelse

data class Skjermingshendelse(override val fnr: String) : Personhendelse
