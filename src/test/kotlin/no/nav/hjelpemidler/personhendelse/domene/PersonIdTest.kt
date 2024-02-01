package no.nav.hjelpemidler.personhendelse.domene

import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class PersonIdTest {
    @Test
    fun `Skal gjøre om tekst til AktørId`() {
        "1234567891011".toPersonId().shouldBeInstanceOf<AktørId>()
    }

    @Test
    fun `Skal gjøre om tekst til Fødselsnummer`() {
        "01028493807".toPersonId().shouldBeInstanceOf<Fødselsnummer>()
    }

    @Test
    fun `Skal gjøre om tekst til Annen`() {
        "19901020ABC".toPersonId().shouldBeInstanceOf<Annen>()
    }
}
