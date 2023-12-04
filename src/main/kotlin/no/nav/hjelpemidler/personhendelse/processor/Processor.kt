package no.nav.hjelpemidler.personhendelse.processor

fun interface Processor<in K, in V, out T> {
    operator fun invoke(key: K, value: V): T
}
