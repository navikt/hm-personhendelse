package no.nav.hjelpemidler.personhendelse

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.treeToValue
import java.util.UUID

val jsonMapper: JsonMapper =
    jacksonMapperBuilder()
        .addModule(JavaTimeModule())
        .build()

fun JsonNode.asUUID(): UUID =
    UUID.fromString(asText())

inline fun <reified T> JsonNode.asObject(): T =
    jsonMapper.treeToValue(this)
