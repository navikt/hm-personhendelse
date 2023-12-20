package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.kafka.stringSerde
import no.nav.hjelpemidler.personhendelse.log.secureLog
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

fun StreamsBuilder.personhendelse(): Map<String, PersonhendelseStream> = this
    .stream(
        Configuration.LEESAH_TOPIC,
        Consumed.with(stringSerde, specificAvroSerde<Personhendelse>())
    )
    .filter { _, personhendelse ->
        val harFnr = personhendelse.harFnr
        if (!harFnr) {
            secureLog.info { "Ignorerer hendelseId: ${personhendelse.hendelseId}, mangler fnr, personidenter: ${personhendelse.personidenter}" }
        }
        harFnr
    }
    .selectKey { _, personhendelse -> personhendelse.fnr }
    .split()
    .adressebeskyttelse()
    .dødsfall()
    .noDefaultBranch()
