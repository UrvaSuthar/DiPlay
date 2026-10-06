package com.shilapi.xcertplay.airplay

import java.io.Closeable
import java.net.InetAddress
import java.net.SocketException

/** android-8.1: some API 26/27 head-unit kernels lack IPv6, so `::` binds throw (upstream #200). */
internal object WildcardBind {
    private val IPV6_ANY: InetAddress = InetAddress.getByName("::")
    private val IPV4_ANY: InetAddress = InetAddress.getByName("0.0.0.0")

    fun <T : Closeable> bind(create: () -> T, bind: (T, InetAddress) -> Unit): T {
        val socket = create()
        try {
            bind(socket, IPV6_ANY)
            return socket
        } catch (_: SocketException) {
            socket.close()
        }
        val fallback = create()
        try {
            bind(fallback, IPV4_ANY)
            return fallback
        } catch (e: SocketException) {
            fallback.close()
            throw e
        }
    }
}
