package no.nav.hjelpemidler.personhendelse.leesah

import mu.KotlinLogging
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.any
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced

private val log = KotlinLogging.logger {}

fun StreamsBuilder.personhendelse() {
    val stringSerde = Serdes.String()
    val personhendelseSerde = specificAvroSerde<Personhendelse>()

    // legg til filter her som inkluderer hendelser vi er interessert i
    val anyOfFilter = listOf(
        personhendelseAdressebeskyttelseFilter,
    ).any()

    // legg til prosessorer her som skal transformere interessante personhendelser til meldinger på rapid
    val processors = listOf<PersonhendelseProcessor<*>>(
        personhendelseAdressebeskyttelseProcessor,
    )

    this
        .stream(
            Configuration.LEESAH_TOPIC,
            Consumed.with(stringSerde, personhendelseSerde)
        )
        .filter(anyOfFilter)
        .peek { _, personhendelse ->
            log.info {
                val informasjon = mapOf(
                    "hendelseId" to personhendelse.hendelseId,
                    "tidligereHendelseId" to personhendelse.tidligereHendelseId,
                    "opprettet" to personhendelse.opprettet,
                    "opplysningstype" to personhendelse.opplysningstype,
                    "endringstype" to personhendelse.endringstype,
                    "master" to personhendelse.master,
                ).map { (key, value) -> "$key: $value" }.joinToString()

                "Mottok personhendelse til prosessering, $informasjon"
            }
        }
        .flatMap { _, personhendelse ->
            val fnr = personhendelse.fnr
            processors
                .mapNotNull { processor -> processor(fnr, personhendelse) }
                .map { event -> KeyValue.pair(fnr.toString(), event) }
        }
        .to(
            Configuration.KAFKA_RAPID_TOPIC,
            Produced.with(stringSerde, jsonSerde()),
        )
}
