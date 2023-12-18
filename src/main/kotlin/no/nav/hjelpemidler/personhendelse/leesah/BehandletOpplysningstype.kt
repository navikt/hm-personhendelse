package no.nav.hjelpemidler.personhendelse.leesah

/**
 * Inneholder opplysningstyper som vi vil behandler videre.
 */
enum class BehandletOpplysningstype(val opplysningstype: String) {
    ADRESSEBESKYTTELSE_V1("ADRESSEBESKYTTELSE_V1"),
    DØDSFALL_V1("DOEDSFALL_V1"),
    ;
}

fun BehandletOpplysningstype?.erAdressebeskyttelse(): Boolean =
    this == BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1

fun BehandletOpplysningstype?.erDødsfall(): Boolean =
    this == BehandletOpplysningstype.DØDSFALL_V1

fun behandletOpplysningstypeOf(opplysningstype: String): BehandletOpplysningstype? =
    BehandletOpplysningstype.entries.singleOrNull { it.opplysningstype == opplysningstype }
