package no.nav.hjelpemidler.personhendelse

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder

val jsonMapper: JsonMapper =
    jacksonMapperBuilder()
        .addModule(JavaTimeModule())
        .build()
