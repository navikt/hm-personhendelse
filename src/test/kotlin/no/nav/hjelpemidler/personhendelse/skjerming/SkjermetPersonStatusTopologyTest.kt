package no.nav.hjelpemidler.personhendelse.skjerming

import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.test.asSequence
import no.nav.hjelpemidler.personhendelse.test.testTopology
import org.apache.kafka.common.serialization.Serdes
import kotlin.test.Test

class SkjermetPersonStatusTopologyTest {
    private val stringSerde = Serdes.String()
    private val skjermetPersonStatusEventSerde = jsonSerde<SkjermetPersonStatusEvent>()

    private val driver = testTopology {
        skjermetPersonStatus()
    }

    private val inputTopic = driver.createInputTopic(
        Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
        stringSerde.serializer(),
        stringSerde.serializer(),
    )
    private val outputTopic = driver.createOutputTopic(
        Configuration.KAFKA_RAPID_TOPIC,
        stringSerde.deserializer(),
        skjermetPersonStatusEventSerde.deserializer(),
    )

    @Test
    fun `Skal transformere melding om skjermet person og sende svaret videre på rapid`() {
        val personId = "12345678910"
        val skjermet = true

        inputTopic.pipeInput(personId, skjermet.toString())

        val record = outputTopic.asSequence().single()
        record.key shouldBe personId

        val value = record.value
        value.fnr shouldBe personId.toFødselsnummer()
        value.skjermet shouldBe skjermet
    }

    @Test
    fun `Skal ignorere melding om skjermet person som ikke har fnr som nøkkel`() {
        val personId = "19901220ABC"
        val skjermet = true

        inputTopic.pipeInput(personId, skjermet.toString())

        outputTopic.asSequence().shouldBeEmpty()
    }
}
