package no.nav.hjelpemidler.personhendelse.leesah

import mu.KotlinLogging
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.GcpEnvironment
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.any
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.log.secureLog
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

    /**
     * Filtre som inkluderer hendelser vi er interessert i.
     */
    val anyOfFilter = listOf(
        personhendelseAdressebeskyttelseFilter,
    ).any()

    /**
     * Prosessorer som skal transformere interessante personhendelser til meldinger på rapid.
     */
    val processors = listOf<PersonhendelseProcessor<*>>(
        personhendelseAdressebeskyttelseProcessor,
    )

    val stream = this
        .stream(
            Configuration.LEESAH_TOPIC,
            Consumed.with(stringSerde, personhendelseSerde)
        )
        .filter(anyOfFilter)
        .peek { _, personhendelse ->
            log.info { "Mottok personhendelse til prosessering, ${personhendelse.informasjon}" }
            secureLog.info { "Mottok personhendelse til prosessering for fnr: ${personhendelse.fnr}, personidenter: ${personhendelse.personidenter}, hendelseId: ${personhendelse.hendelseId}" }
        }
        .flatMap { _, personhendelse ->
            val fnr = personhendelse.fnr
            processors
                .mapNotNull { processor -> processor(fnr, personhendelse) }
                .map { event -> KeyValue.pair(fnr.toString(), event) }
        }

    if (Environment.current != GcpEnvironment.PROD) {
        stream
            .to(
                Configuration.KAFKA_RAPID_TOPIC,
                Produced.with(stringSerde, jsonSerde()),
            )
    }
}
