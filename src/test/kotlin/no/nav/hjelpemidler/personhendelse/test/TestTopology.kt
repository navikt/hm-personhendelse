package no.nav.hjelpemidler.personhendelse.test

import no.nav.hjelpemidler.streams.topology
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TopologyTestDriver

fun testTopology(block: StreamsBuilder.() -> Unit): TopologyTestDriver =
    TopologyTestDriver(topology(block))
