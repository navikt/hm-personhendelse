package no.nav.hjelpemidler.personhendelse.leesah

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.logging.teamInfo
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.streams.kstream.BranchedKStream
import org.apache.kafka.streams.kstream.KStream

private val log = KotlinLogging.logger {}

typealias PersonhendelseStream = KStream<Fødselsnummer, Personhendelse>
typealias PersonhendelseBranchedStream = BranchedKStream<Fødselsnummer, Personhendelse>

fun PersonhendelseStream.log(): PersonhendelseStream = peek { _, personhendelse ->
    log.info { "Mottok personhendelse til prosessering, ${personhendelse.sammendrag}" }
    log.teamInfo { "Mottok personhendelse til prosessering for fnr: ${personhendelse.fnr}, personidenter: ${personhendelse.personidenter}, hendelseId: ${personhendelse.hendelseId}" }
}
