package no.nav.hjelpemidler.personhendelse.kafka

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.hjelpemidler.configuration.Environment
import no.nav.hjelpemidler.configuration.LocalEnvironment
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.jsonMapper
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes

fun genericAvroSerde(): Serde<GenericRecord> = when (Environment.current) {
    LocalEnvironment -> GenericAvroSerde(MockSchemaRegistryClient())
    else -> GenericAvroSerde().apply {
        configure(Configuration.kafkaSchemaRegistryConfiguration(), false)
    }
}

fun <T : SpecificRecord> specificAvroSerde(): Serde<T> = when (Environment.current) {
    LocalEnvironment -> SpecificAvroSerde(MockSchemaRegistryClient())
    else -> SpecificAvroSerde<T>().apply {
        configure(Configuration.kafkaSchemaRegistryConfiguration(), false)
    }
}

inline fun <reified T> jsonSerde(): Serde<T> {
    val typeReference = jacksonTypeRef<T>()
    val writer = jsonMapper.writerFor(typeReference)
    val reader = jsonMapper.readerFor(typeReference)
    return Serdes.serdeFrom(
        { _, data ->
            when (data) {
                null -> null
                else -> writer.writeValueAsBytes(data)
            }
        },
        { _, data ->
            when (data) {
                null -> null
                else -> reader.readValue<T>(data)
            }
        },
    )
}
