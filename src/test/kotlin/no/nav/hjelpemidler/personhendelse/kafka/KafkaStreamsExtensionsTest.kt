package no.nav.hjelpemidler.personhendelse.kafka

import io.kotest.matchers.shouldBe
import org.apache.kafka.streams.kstream.Predicate
import kotlin.test.Test

class KafkaStreamsExtensionsTest {
    private val alwaysTrue = Predicate<String, String> { _, _ -> true }
    private val alwaysFalse = Predicate<String, String> { _, _ -> false }

    @Test
    fun `Sann hvis ett av predikatene svarer med sann`() {
        val predicate = listOf(alwaysFalse, alwaysTrue, alwaysFalse).any()

        predicate("", "") shouldBe true
    }

    @Test
    fun `Usann hvis ingen av predikatene svarer med sann`() {
        val predicate = listOf(alwaysFalse, alwaysFalse, alwaysFalse).any()

        predicate("", "") shouldBe false
    }

    @Test
    fun `Usann hvis ikke alle predikatene svarer med sann`() {
        val predicate = listOf(alwaysFalse, alwaysTrue, alwaysFalse).all()

        predicate("", "") shouldBe false
    }

    @Test
    fun `Sann hvis alle predikatene svarer med sann`() {
        val predicate = listOf(alwaysTrue, alwaysTrue, alwaysTrue).all()

        predicate("", "") shouldBe true
    }
}
