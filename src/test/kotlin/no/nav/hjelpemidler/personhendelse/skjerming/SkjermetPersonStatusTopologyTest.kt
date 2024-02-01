package no.nav.hjelpemidler.personhendelse.skjerming

import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.lagFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.kafka.stringSerde
import no.nav.hjelpemidler.personhendelse.test.asSequence
import no.nav.hjelpemidler.personhendelse.test.testTopology
import kotlin.test.Test

class SkjermetPersonStatusTopologyTest {
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
        val personId = lagFødselsnummer(50)
        val skjermet = true

        inputTopic.pipeInput(personId.toString(), skjermet.toString())

        val record = outputTopic.asSequence().single()
        record.key shouldBe personId.toString()

        val value = record.value
        value.fnr shouldBe personId
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
