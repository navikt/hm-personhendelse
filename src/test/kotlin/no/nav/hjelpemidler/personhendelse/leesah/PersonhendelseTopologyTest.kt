package no.nav.hjelpemidler.personhendelse.leesah

import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.specificAvroSerde
import no.nav.hjelpemidler.personhendelse.test.asSequence
import no.nav.hjelpemidler.personhendelse.test.testTopology
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import org.apache.kafka.common.serialization.Serdes
import kotlin.test.Test

class PersonhendelseTopologyTest {
    private val stringSerde = Serdes.String()
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
        val fnr = "12345678910"

        inputTopic.pipeInput(fnr, lagPersonhendelse(fnr) {
            opplysningstype = "TEST_V1"
            endringstype = Endringstype.OPPRETTET
            adressebeskyttelse = null
        })

        outputTopic.asSequence().shouldBeEmpty()
    }

    @Test
    fun `Skal transformere melding om opprettet adressebeskyttelse og sende svaret videre på rapid`() {
        val fnr = "12345678910"
        val gradering = Gradering.STRENGT_FORTROLIG

        inputTopic.pipeInput(fnr, lagPersonhendelse(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1
            endringstype = Endringstype.OPPRETTET
            adressebeskyttelse = Adressebeskyttelse(gradering)
        })

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr

        val value = record.value.shouldBeInstanceOf<PersonhendelseAdressebeskyttelseEvent>()
        value.fnr shouldBe fnr.toFødselsnummer()
        value.gradering shouldBe gradering
    }

    @Test
    fun `Skal transformere melding om annulert adressebeskyttelse og sende svaret videre på rapid`() {
        val fnr = "12345678910"

        inputTopic.pipeInput(fnr, lagPersonhendelse(fnr) {
            behandletOpplysningstype = BehandletOpplysningstype.ADRESSEBESKYTTELSE_V1
            endringstype = Endringstype.ANNULLERT
            adressebeskyttelse = null
        })

        val record = outputTopic.asSequence().single()
        record.key shouldBe fnr

        val value = record.value.shouldBeInstanceOf<PersonhendelseAdressebeskyttelseEvent>()
        value.fnr shouldBe fnr.toFødselsnummer()
        value.gradering shouldBe null
    }
}
