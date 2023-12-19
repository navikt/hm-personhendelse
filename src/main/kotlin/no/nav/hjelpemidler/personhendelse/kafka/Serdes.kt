package no.nav.hjelpemidler.personhendelse.kafka

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.hjelpemidler.personhendelse.Configuration
import no.nav.hjelpemidler.personhendelse.domene.Fødselsnummer
import no.nav.hjelpemidler.personhendelse.domene.toFødselsnummer
import no.nav.hjelpemidler.personhendelse.jsonMapper
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes

val stringSerde: Serde<String> = Serdes.String()

val fødselsnummerSerde: Serde<Fødselsnummer?> = Serdes.serdeFrom(
    { _, data -> data?.toString()?.toByteArray(Charsets.UTF_8) },
    { _, data -> data?.toString(Charsets.UTF_8)?.toFødselsnummer() },
)

fun genericAvroSerde(): Serde<GenericRecord> = GenericAvroSerde().apply {
    configure(Configuration.kafkaSchemaRegistryConfiguration(), false)
}

fun <T : SpecificRecord> specificAvroSerde(): Serde<T> = SpecificAvroSerde<T>().apply {
    configure(Configuration.kafkaSchemaRegistryConfiguration(), false)
}

@Suppress("RemoveExplicitTypeArguments")
inline fun <reified T> jsonSerde(): Serde<T> {
    val typeReference = jacksonTypeRef<T>()
    return Serdes.serdeFrom(
        { _, data ->
            when (data) {
                null -> null
                else -> jsonMapper.writeValueAsBytes(data)
            }
        },
        { _, data ->
            when (data) {
                null -> null
                else -> jsonMapper.readValue<T>(data, typeReference)
            }
        },
    )
}
