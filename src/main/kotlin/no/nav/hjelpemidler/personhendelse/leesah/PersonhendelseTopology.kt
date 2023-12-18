package no.nav.hjelpemidler.personhendelse.leesah

import mu.KotlinLogging
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.GcpEnvironment
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.any
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.kafka.toIf
import no.nav.hjelpemidler.personhendelse.kafka.withKey
import no.nav.hjelpemidler.personhendelse.log.secureLog
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Branched
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
        personhendelseDødsfallFilter,
    ).any()

    this
        .stream(
            Configuration.LEESAH_TOPIC,
            Consumed.with(stringSerde, personhendelseSerde)
        )
        .filter { _, personhendelse -> personhendelse.harFnr }
        .selectKey { _, personhendelse -> personhendelse.fnr }
        .filter(anyOfFilter)
        .peek { _, personhendelse ->
            log.info { "Mottok personhendelse til prosessering, ${personhendelse.sammendrag}" }
            secureLog.info { "Mottok personhendelse til prosessering for fnr: ${personhendelse.fnr}, personidenter: ${personhendelse.personidenter}, hendelseId: ${personhendelse.hendelseId}" }
        }
        .split()
        .branch(
            personhendelseAdressebeskyttelseFilter,
            Branched.withConsumer { branch ->
                branch
                    .map { fnr, personhendelse ->
                        val event = personhendelseAdressebeskyttelseProcessor(fnr, personhendelse)
                        event withKey fnr.toString()
                    }
                    .toIf(
                        Environment.current != GcpEnvironment.PROD,
                        Configuration.KAFKA_RAPID_TOPIC,
                        Produced.with(stringSerde, jsonSerde()),
                    )
            },
        )
        .branch(
            personhendelseDødsfallFilter,
            Branched.withConsumer { branch ->
                branch
                    .map { fnr, personhendelse ->
                        val event = personhendelseDødsfallProcessor(fnr, personhendelse)
                        event withKey fnr.toString()
                    }
                    .toIf(
                        Environment.current != GcpEnvironment.PROD,
                        Configuration.KAFKA_RAPID_TOPIC,
                        Produced.with(stringSerde, jsonSerde()),
                    )
            },
        )
        .noDefaultBranch()
}
