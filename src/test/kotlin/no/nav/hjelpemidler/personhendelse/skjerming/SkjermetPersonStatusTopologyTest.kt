package no.nav.hjelpemidler.personhendelse.skjerming

import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.hjelpemidler.domain.person.Fødselsnummer
import no.nav.hjelpemidler.domain.person.år
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.test.asSequence
import no.nav.hjelpemidler.personhendelse.test.testTopology
import no.nav.hjelpemidler.streams.serialization.jsonSerde
import no.nav.hjelpemidler.streams.serialization.serde
import kotlin.test.Test

class SkjermetPersonStatusTopologyTest {
    private val skjermetPersonStatusEventSerde = jsonSerde<SkjermetPersonStatusEvent>()

    private val driver = testTopology {
        skjermetPersonStatus()
    }

    private val inputTopic = driver.createInputTopic(
        Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
        serde<String>().serializer(),
        serde<String>().serializer(),
    )
    private val outputTopic = driver.createOutputTopic(
        Configuration.KAFKA_RAPID_TOPIC,
        serde<String>().deserializer(),
        skjermetPersonStatusEventSerde.deserializer(),
    )

    @Test
    fun `Skal transformere melding om skjermet person og sende svaret videre på rapid`() {
        val ident = Fødselsnummer(50.år)
        val skjermet = true

        inputTopic.pipeInput(ident.toString(), skjermet.toString())

        val record = outputTopic.asSequence().single()
        record.key shouldBe ident.toString()

        val value = record.value
        value.fnr shouldBe ident
        value.skjermet shouldBe skjermet
    }

    @Test
    fun `Skal ignorere melding om skjermet person som ikke har fnr som nøkkel`() {
        val ident = "19901220ABC"
        val skjermet = true

        inputTopic.pipeInput(ident, skjermet.toString())

        outputTopic.asSequence().shouldBeEmpty()
    }
}
