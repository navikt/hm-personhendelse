package no.nav.hjelpemidler.personhendelse.leesah

import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.kafka.Processor
import no.nav.person.pdl.leesah.Personhendelse

fun interface PersonhendelseProcessor<out T : PersonhendelseEvent?> : Processor<Fødselsnummer, Personhendelse, T>
