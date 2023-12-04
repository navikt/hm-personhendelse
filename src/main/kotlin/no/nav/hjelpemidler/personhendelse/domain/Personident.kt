package no.nav.hjelpemidler.personhendelse.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

sealed interface Personident {
    @get:JsonValue
    val value: String
}

fun String.toPersonident(): Personident = when {
    AktørId.isValid(this) -> AktørId(this)
    Fødselsnummer.isValid(this) -> Fødselsnummer(this)
    else -> Ukjent(this)
}

data class Ukjent @JsonCreator constructor(override val value: String) : Personident {
    override fun toString(): String = value
}

data class AktørId @JsonCreator constructor(override val value: String) : Personident {
    init {
        require(isValid(value)) { "$value er ugyldig" }
    }

    override fun toString(): String = value

    companion object {
        private val regex = "^[0-9]{13}\$".toRegex()
        fun isValid(value: String): Boolean = value matches regex
    }
}

fun String.toAktørId(): AktørId = AktørId(this)

data class Fødselsnummer @JsonCreator constructor(override val value: String) : Personident {
    init {
        require(isValid(value)) { "$value er ugyldig" }
    }

    override fun toString(): String = value

    companion object {
        private val regex = "^[0-9]{11}\$".toRegex()
        fun isValid(value: String): Boolean = value matches regex
    }
}

fun String.toFødselsnummer(): Fødselsnummer = Fødselsnummer(this)
