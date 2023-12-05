package no.nav.hjelpemidler.personhendelse.leesah

enum class BehandletOpplysningstype(val opplysningstype: String) {
    ADRESSEBESKYTTELSE("ADRESSEBESKYTTELSE_V1"),
    ;
}

fun BehandletOpplysningstype?.erAdressebeskyttelse(): Boolean =
    this == BehandletOpplysningstype.ADRESSEBESKYTTELSE

fun behandletOpplysningstypeOf(opplysningstype: String): BehandletOpplysningstype? =
    BehandletOpplysningstype.entries.singleOrNull { it.opplysningstype == opplysningstype }
