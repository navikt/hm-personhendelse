package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.kafka.stringSerde
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed

fun StreamsBuilder.personhendelse(): Map<String, PersonhendelseStream> = this
    .stream(
        Configuration.LEESAH_TOPIC,
        Consumed.with(stringSerde, specificAvroSerde<Personhendelse>())
    )
    .filter { _, personhendelse -> personhendelse.harFnr }
    .selectKey { _, personhendelse -> personhendelse.fnr }
    .split()
    .adressebeskyttelse()
    .dødsfall()
    .noDefaultBranch()
