package no.nav.hjelpemidler.personhendelse.domene

import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class PersonidentTest {
    @Test
    fun `Skal gjøre om tekst til aktørId`() {
        "1234567891011".toPersonident().shouldBeInstanceOf<AktørId>()
    }

    @Test
    fun `Skal gjøre om tekst til fødselsnummer`() {
        "12345678910".toPersonident().shouldBeInstanceOf<Fødselsnummer>()
    }

    @Test
    fun `Skal gjøre om tekst til ukjent`() {
        "123456789".toPersonident().shouldBeInstanceOf<Ukjent>()
    }
}