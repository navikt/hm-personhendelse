package no.nav.hjelpemidler.personhendelse.leesah

enum class BehandletOpplysningstype(val opplysningstype: String) {
    ADRESSEBESKYTTELSE_V1("ADRESSEBESKYTTELSE_V1"),
    ;
}

fun BehandletOpplysningstype?.erAdressebeskyttelse(): Boolean =
    this == BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1

fun behandletOpplysningstypeOf(opplysningstype: String): BehandletOpplysningstype? =
    BehandletOpplysningstype.entries.singleOrNull { it.opplysningstype == opplysningstype }
