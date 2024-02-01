package no.nav.hjelpemidler.personhendelse.domene

import no.bekk.bekkopen.person.Fodselsnummer
import no.bekk.bekkopen.person.FodselsnummerCalculator
import java.time.ZonedDateTime
import java.util.Date

fun lagFødselsnummer(alder: Int = 40): Fødselsnummer = FodselsnummerCalculator
    .getFodselsnummerForDate(ZonedDateTime.now().minusYears(alder.toLong()).toInstant().let(Date::from))
    .let(Fodselsnummer::getValue)
    .let(::Fødselsnummer)
