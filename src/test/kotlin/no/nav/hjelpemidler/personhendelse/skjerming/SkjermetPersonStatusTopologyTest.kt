package no.nav.hjelpemidler.personhendelse.skjerming

import io.kotest.matchers.shouldBe
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
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
        val fnr = "12345678910"
        val erSkjermet = true

        inputTopic.pipeInput(fnr, erSkjermet.toString())

        val outputEvent = outputTopic.readKeyValue()
        outputEvent.key shouldBe fnr

        val outputValue = outputEvent.value
        outputValue.fnr shouldBe fnr.toFødselsnummer()
        outputValue.erSkjermet shouldBe erSkjermet

        outputTopic.isEmpty shouldBe true
    }
}
