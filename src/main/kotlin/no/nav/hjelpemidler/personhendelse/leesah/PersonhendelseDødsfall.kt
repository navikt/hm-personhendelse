package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.toRapid
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.kstream.Branched
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun PersonhendelseBranchedStream.dødsfall(): PersonhendelseBranchedStream = branch(
    behandletOpplysningstypeFilter(BehandletOpplysningstype.DØDSFALL_V1),
    Branched.withConsumer { stream ->
        stream
            .log()
            .mapValues(::PersonhendelseDødsfallEvent)
            .toRapid()
    },
)

data class PersonhendelseDødsfallEvent(
    override val kilde: PersonhendelseEvent.Kilde,
    val fnr: Fødselsnummer,
    val dødsdato: LocalDate?,
) : PersonhendelseEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val eventName: String = "hm-personhendelse-dødsfall"
    override val opprettet: Instant = Instant.now()

    constructor(fnr: Fødselsnummer, personhendelse: Personhendelse) : this(
        kilde = personhendelse.kilde,
        fnr = fnr,
        dødsdato = when (val endringstype = personhendelse.endringstype) {
            Endringstype.OPPRETTET,
            Endringstype.KORRIGERT,
                -> personhendelse.doedsfall.doedsdato

            Endringstype.ANNULLERT,
            Endringstype.OPPHOERT,
                -> null

            else -> error("Ukjent endringstype: $endringstype")
        },
    )
}
