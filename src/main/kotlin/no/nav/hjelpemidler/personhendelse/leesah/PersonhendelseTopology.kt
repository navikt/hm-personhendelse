package no.nav.hjelpemidler.personhendelse.leesah

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.logging.teamInfo
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.streams.serialization.serde
import no.nav.hjelpemidler.streams.serialization.specificAvroSerde
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

private val log = KotlinLogging.logger {}

fun StreamsBuilder.personhendelse(): Map<String, PersonhendelseStream> = this
    .stream(
        Configuration.LEESAH_TOPIC,
        Consumed.with(serde<String>(), specificAvroSerde<Personhendelse>())
    )
    .filter { _, personhendelse ->
        val harFnr = personhendelse.harFnr
        if (!harFnr) {
            log.teamInfo { "Ignorerer hendelseId: ${personhendelse.hendelseId}, mangler fnr, personidenter: ${personhendelse.personidenter}" }
        }
        harFnr
    }
    .selectKey { _, personhendelse -> personhendelse.fnr }
    .split()
    .adressebeskyttelse()
    .dødsfall()
    .noDefaultBranch()
