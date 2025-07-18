package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.kafka.KafkaEvent
import no.nav.hjelpemidler.streams.serialization.fødselsnummerSerde
import no.nav.hjelpemidler.streams.toRapid
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.kstream.Branched
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun PersonhendelseBranchedStream.dødsfall(): PersonhendelseBranchedStream =
    branch(
        behandletOpplysningstypeFilter(BehandletOpplysningstype.DØDSFALL_V1),
        Branched.withConsumer { stream ->
            stream
                .log()
                .mapValues(::PersonhendelseDødsfallEvent)
                .toRapid<Fødselsnummer, PersonhendelseDødsfallEvent>(fødselsnummerSerde())
        },
    )

@KafkaEvent("hm-personhendelse-dødsfall")
data class PersonhendelseDødsfallEvent(
    override val kilde: PersonhendelseEvent.Kilde,
    val fnr: Fødselsnummer,
    val dødsdato: LocalDate?,
    val opprettet: Instant = Instant.now(),
    override val eventId: UUID = UUID.randomUUID(),
) : PersonhendelseEvent {
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
