package no.nav.hjelpemidler.personhendelse.kafka

import no.nav.hjelpemidler.personhendelse.domene.Event

fun interface Processor<in K, in V, out T : Event?> {
    operator fun invoke(key: K, value: V): T
}
