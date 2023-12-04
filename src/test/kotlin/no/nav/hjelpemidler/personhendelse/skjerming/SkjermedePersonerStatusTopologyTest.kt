package no.nav.hjelpemidler.personhendelse.skjerming

import io.kotest.matchers.shouldBe
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domain.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.jsonSerde
import no.nav.hjelpemidler.personhendelse.test.testTopology
import org.apache.kafka.common.serialization.Serdes
import kotlin.test.Test

class SkjermedePersonerStatusTopologyTest {
    private val stringSerde = Serdes.String()

    @Test
    fun `Skal transformere melding om skjermet person og sende svaret videre på rapid`() {
        val fnr = "12345678910"
        val erSkjermet = true

        val driver = testTopology {
            skjermedePersonerStatus()
        }
        val inputTopic = driver.createInputTopic(
            Configuration.SKJERMEDE_PERSONER_STATUS_TOPIC,
            stringSerde.serializer(),
            stringSerde.serializer(),
        )
        val outputTopic = driver.createOutputTopic(
            Configuration.KAFKA_RAPID_TOPIC,
            stringSerde.deserializer(),
            jsonSerde<SkjermetPersonStatusEvent>().deserializer(),
        )

        inputTopic.pipeInput(fnr, erSkjermet.toString())

        val outputEvent = outputTopic.readKeyValue()
        outputEvent.key shouldBe fnr

        val outputValue = outputEvent.value
        outputValue.eventName shouldBe "hm-personhendelse-skjermet-person-status"
        outputValue.data.fnr shouldBe fnr.toFødselsnummer()
        outputValue.data.erSkjermet shouldBe erSkjermet

        outputTopic.isEmpty shouldBe true
    }
}
