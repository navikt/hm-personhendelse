package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.kstream.Predicate

/**
 * Inneholder opplysningstyper som vi vil behandle videre.
 */
enum class BehandletOpplysningstype(val opplysningstype: String) {
    ADRESSEBESKYTTELSE_V1("ADRESSEBESKYTTELSE_V1"),
    DØDSFALL_V1("DOEDSFALL_V1"),
    ;
}

fun behandletOpplysningstypeOf(opplysningstype: String): BehandletOpplysningstype? =
    BehandletOpplysningstype.entries.singleOrNull { it.opplysningstype == opplysningstype }

fun behandletOpplysningstypeFilter(behandletOpplysningstype: BehandletOpplysningstype): Predicate<Fødselsnummer, Personhendelse> =
    Predicate { _, personhendelse ->
        personhendelse.behandletOpplysningstype == behandletOpplysningstype
    }
