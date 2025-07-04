package no.nav.hjelpemidler.personhendelse

import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.domain.person.TILLAT_SYNTETISKE_FØDSELSNUMRE
import no.nav.hjelpemidler.personhendelse.leesah.personhendelse
import no.nav.hjelpemidler.personhendelse.skjerming.skjermetPersonStatus
import no.nav.hjelpemidler.streams.kafkaStreamsApplication

fun main() {
    TILLAT_SYNTETISKE_FØDSELSNUMRE = !Environment.current.isProd

    kafkaStreamsApplication(
        applicationId = Configuration.KAFKA_APPLICATION_ID,
        port = Configuration.HTTP_PORT,
    ) {
        topology {
            personhendelse()
            skjermetPersonStatus()
        }
    }.start()
}
