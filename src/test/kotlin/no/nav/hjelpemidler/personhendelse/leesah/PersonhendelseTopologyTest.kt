package no.nav.hjelpemidler.personhendelse.leesah

import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.år
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.kafka.stringSerde
import no.nav.hjelpemidler.personhendelse.test.asSequence
import no.nav.hjelpemidler.personhendelse.test.testTopology
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import no.nav.person.pdl.leesah.doedsfall.Doedsfall
import org.apache.kafka.streams.TestInputTopic
import java.time.LocalDate
import kotlin.test.Test

class PersonhendelseTopologyTest {
    private val personhendelseSerde = specificAvroSerde<Personhendelse>()
    private val personhendelseEventSerde = jsonSerde<PersonhendelseEvent>()

    private val driver = testTopology {
        personhendelse()
    }

    private val inputTopic = driver.createInputTopic(
        Configuration.LEESAH_TOPIC,
        stringSerde.serializer(),
        personhendelseSerde.serializer(),
    )
    private val outputTopic = driver.createOutputTopic(
        Configuration.KAFKA_RAPID_TOPIC,
        stringSerde.deserializer(),
        personhendelseEventSerde.deserializer(),
    )

    @Test
    fun `Skal filtrere vekk melding som ikke skal sendes videre på rapid`() {
        val fnr = Fødselsnummer(10.år)

        inputTopic.pipeInput(fnr) {
            opplysningstype = "TEST_V1"
            endringstype = Endringstype.OPPRETTET
            adressebeskyttelse = null
        }

        outputTopic.asSequence().shouldBeEmpty()
    }

    @Test
    fun `Skal transformere melding om opprettet adressebeskyttelse og sende svaret videre på rapid`() {
        val fnr = Fødselsnummer(20.år)
        val gradering = Gradering.STRENGT_FORTROLIG

        inputTopic.pipeInput(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1
            endringstype = Endringstype.OPPRETTET
            adressebeskyttelse = Adressebeskyttelse(gradering)
        }

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr.toString()

        val value = record.value.shouldBeInstanceOf<PersonhendelseAdressebeskyttelseEvent>()
        value.fnr shouldBe fnr
        value.gradering shouldBe gradering
    }

    @Test
    fun `Skal transformere melding om annulert adressebeskyttelse og sende svaret videre på rapid`() {
        val fnr = Fødselsnummer(30.år)

        inputTopic.pipeInput(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1
            endringstype = Endringstype.ANNULLERT
            adressebeskyttelse = null
        }

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr.toString()

        val value = record.value.shouldBeInstanceOf<PersonhendelseAdressebeskyttelseEvent>()
        value.fnr shouldBe fnr
        value.gradering shouldBe null
    }

    @Test
    fun `Skal transformere melding om opprettet dødsfall og sende svaret videre på rapid`() {
        val fnr = Fødselsnummer(40.år)
        val dødsdato = LocalDate.now()

        inputTopic.pipeInput(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.DØDSFALL_V1
            endringstype = Endringstype.OPPRETTET
            doedsfall = Doedsfall(dødsdato)
        }

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr.toString()

        val value = record.value.shouldBeInstanceOf<PersonhendelseDødsfallEvent>()
        value.fnr shouldBe fnr
        value.dødsdato shouldBe dødsdato
    }

    @Test
    fun `Skal transformere melding om annulert dødsfall og sende svaret videre på rapid`() {
        val fnr = Fødselsnummer(50.år)

        inputTopic.pipeInput(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.DØDSFALL_V1
            endringstype = Endringstype.ANNULLERT
            doedsfall = null
        }

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr.toString()

        val value = record.value.shouldBeInstanceOf<PersonhendelseDødsfallEvent>()
        value.fnr shouldBe fnr
        value.dødsdato shouldBe null
    }
}

private fun TestInputTopic<String, Personhendelse>.pipeInput(
    fnr: Fødselsnummer,
    block: Personhendelse.() -> Unit,
): Personhendelse {
    val personhendelse = lagPersonhendelse(fnr, block)
    pipeInput(fnr.toString(), personhendelse)
    return personhendelse
}
