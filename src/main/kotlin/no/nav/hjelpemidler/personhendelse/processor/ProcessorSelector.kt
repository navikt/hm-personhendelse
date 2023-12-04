package no.nav.hjelpemidler.personhendelse.processor

fun interface ProcessorSelector<in K, in V> {
    operator fun invoke(key: K, value: V): Boolean
}
