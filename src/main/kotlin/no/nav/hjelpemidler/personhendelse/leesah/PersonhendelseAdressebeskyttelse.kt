package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import org.apache.kafka.streams.kstream.Predicate
import java.time.Instant
import java.util.UUID

val personhendelseAdressebeskyttelseFilter: Predicate<Fødselsnummer, Personhendelse> = Predicate { _, personhendelse ->
    personhendelse.behandletOpplysningstype.erAdressebeskyttelse()
}

val personhendelseAdressebeskyttelseProcessor: PersonhendelseProcessor<PersonhendelseAdressebeskyttelseEvent?> =
    PersonhendelseProcessor { fnr, personhendelse ->
        when (val endringstype = personhendelse.endringstype) {
            Endringstype.OPPRETTET,
            Endringstype.KORRIGERT -> PersonhendelseAdressebeskyttelseEvent(
                personhendelse = personhendelse,
                fnr = fnr,
                gradering = personhendelse.adressebeskyttelse.gradering,
            )

            Endringstype.ANNULLERT,
            Endringstype.OPPHOERT -> PersonhendelseAdressebeskyttelseEvent(
                personhendelse = personhendelse,
                fnr = fnr,
                gradering = null,
            )

            else -> error("Ukjent endringstype: $endringstype")
        }
    }

data class PersonhendelseAdressebeskyttelseEvent(
    override val hendelseId: String,
    override val tidligereHendelseId: String?,
    override val opplysningstype: String,
    override val endringstype: Endringstype,
    override val opprettet: Instant,
    val fnr: Fødselsnummer,
    val gradering: Gradering?,
) : PersonhendelseEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-adressebeskyttelse"

    constructor(personhendelse: Personhendelse, fnr: Fødselsnummer, gradering: Gradering?) : this(
        hendelseId = personhendelse.hendelseId,
        tidligereHendelseId = personhendelse.tidligereHendelseId,
        opplysningstype = personhendelse.opplysningstype,
        endringstype = personhendelse.endringstype,
        opprettet = personhendelse.opprettet,
        fnr = fnr,
        gradering = gradering,
    )
}
