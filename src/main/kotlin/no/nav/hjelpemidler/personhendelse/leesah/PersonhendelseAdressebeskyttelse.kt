package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.kafka.KafkaEvent
import no.nav.hjelpemidler.streams.serialization.fødselsnummerSerde
import no.nav.hjelpemidler.streams.toRapid
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import org.apache.kafka.streams.kstream.Branched
import java.time.Instant
import java.util.UUID

fun PersonhendelseBranchedStream.adressebeskyttelse(): PersonhendelseBranchedStream =
    branch(
        behandletOpplysningstypeFilter(BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1),
        Branched.withConsumer { stream ->
            stream
                .log()
                .mapValues(::PersonhendelseAdressebeskyttelseEvent)
                .toRapid<Fødselsnummer, PersonhendelseAdressebeskyttelseEvent>(fødselsnummerSerde())
        },
    )

@KafkaEvent("hm-personhendelse-adressebeskyttelse")
data class PersonhendelseAdressebeskyttelseEvent(
    override val kilde: PersonhendelseEvent.Kilde,
    val fnr: Fødselsnummer,
    val gradering: Gradering?,
    val opprettet: Instant = Instant.now(),
    override val eventId: UUID = UUID.randomUUID(),
) : PersonhendelseEvent {
    constructor(fnr: Fødselsnummer, personhendelse: Personhendelse) : this(
        kilde = personhendelse.kilde,
        fnr = fnr,
        gradering = when (val endringstype = personhendelse.endringstype) {
            Endringstype.OPPRETTET,
            Endringstype.KORRIGERT,
                -> personhendelse.adressebeskyttelse.gradering

            Endringstype.ANNULLERT,
            Endringstype.OPPHOERT,
                -> null

            else -> error("Ukjent endringstype: $endringstype")
        },
    )
}
