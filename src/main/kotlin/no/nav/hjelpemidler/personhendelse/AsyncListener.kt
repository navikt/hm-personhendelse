package no.nav.hjelpemidler.personhendelse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.River.PacketListener

sealed class AsyncListener : PacketListener {
    override fun onPacket(packet: JsonMessage, context: MessageContext) =
        runBlocking(Dispatchers.IO) {
            onPacketAsync(packet, context)
        }

    abstract suspend fun onPacketAsync(packet: JsonMessage, context: MessageContext)
}
