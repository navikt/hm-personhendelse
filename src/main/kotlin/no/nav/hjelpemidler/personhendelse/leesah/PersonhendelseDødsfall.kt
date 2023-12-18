package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.kstream.Predicate
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

val personhendelseDødsfallFilter: Predicate<Fødselsnummer, Personhendelse> = Predicate { _, personhendelse ->
    personhendelse.behandletOpplysningstype.erDødsfall()
}

val personhendelseDødsfallProcessor: PersonhendelseProcessor<PersonhendelseDødsfallEvent?> =
    PersonhendelseProcessor { fnr, personhendelse ->
        when (val endringstype = personhendelse.endringstype) {
            Endringstype.OPPRETTET,
            Endringstype.KORRIGERT -> PersonhendelseDødsfallEvent(
                personhendelse = personhendelse,
                fnr = fnr,
                dødsdato = personhendelse.doedsfall.doedsdato
            )

            Endringstype.ANNULLERT,
            Endringstype.OPPHOERT -> PersonhendelseDødsfallEvent(
                personhendelse = personhendelse,
                fnr = fnr,
                dødsdato = null,
            )

            else -> error("Ukjent endringstype: $endringstype")
        }
    }

data class PersonhendelseDødsfallEvent(
    override val hendelseId: String,
    override val tidligereHendelseId: String?,
    override val opplysningstype: String,
    override val endringstype: Endringstype,
    override val opprettet: Instant,
    val fnr: Fødselsnummer,
    val dødsdato: LocalDate?,
) : PersonhendelseEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-dødsfall"

    constructor(personhendelse: Personhendelse, fnr: Fødselsnummer, dødsdato: LocalDate?) : this(
        hendelseId = personhendelse.hendelseId,
        tidligereHendelseId = personhendelse.tidligereHendelseId,
        opplysningstype = personhendelse.opplysningstype,
        endringstype = personhendelse.endringstype,
        opprettet = personhendelse.opprettet,
        fnr = fnr,
        dødsdato = dødsdato,
    )
}
