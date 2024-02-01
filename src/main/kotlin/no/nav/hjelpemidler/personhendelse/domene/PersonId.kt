package no.nav.hjelpemidler.personhendelse.domene

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import no.bekk.bekkopen.person.FodselsnummerValidator

sealed interface PersonId {
    val value: String

    @JsonValue
    override fun toString(): String
}

fun String.toPersonId(): PersonId = when {
    AktørId.isValid(this) -> toAktørId()
    Fødselsnummer.isValid(this) -> toFødselsnummer()
    else -> Annen(this)
}

data class Annen @JsonCreator constructor(override val value: String) : PersonId {
    override fun toString(): String = value
}

data class AktørId @JsonCreator constructor(override val value: String) : PersonId {
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

/**
 * NB! Kan også være D-nummer.
 */
data class Fødselsnummer @JsonCreator constructor(override val value: String) : PersonId {
    init {
        require(isValid(value)) { "$value er ugyldig" }
    }

    override fun toString(): String = value

    companion object {
        fun isValid(value: String): Boolean = FodselsnummerValidator.isValid(value)
    }
}

fun String.toFødselsnummer(): Fødselsnummer = Fødselsnummer(this)
