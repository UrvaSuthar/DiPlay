package com.shilapi.xcertplay.airplay

import java.io.Closeable
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.SocketException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class WildcardBindTest {
    private class FakeSocket : Closeable {
        var bound: InetAddress? = null
        var closed = false
        override fun close() { closed = true }
    }

    @Test
    fun bindsIpv6WildcardWhenAvailable() {
        val socket = WildcardBind.bind(::FakeSocket) { s, address -> s.bound = address }
        assertTrue(socket.bound is Inet6Address)
    }

    @Test
    fun fallsBackToIpv4WhenIpv6Unsupported() {
        val created = mutableListOf<FakeSocket>()
        val socket = WildcardBind.bind({ FakeSocket().also(created::add) }) { s, address ->
            if (address is Inet6Address) throw SocketException("Address family not supported by protocol")
            s.bound = address
        }
        assertTrue(socket.bound is Inet4Address)
        assertEquals(2, created.size)
        assertTrue(created[0].closed)
    }

    @Test
    fun rethrowsAndClosesWhenBothFamiliesFail() {
        val created = mutableListOf<FakeSocket>()
        val ipv4Error = SocketException("ipv4 failed")
        val thrown = runCatching {
            WildcardBind.bind({ FakeSocket().also(created::add) }) { _, address ->
                throw if (address is Inet6Address) SocketException("ipv6 failed") else ipv4Error
            }
        }.exceptionOrNull()
        assertSame(ipv4Error, thrown)
        assertTrue(created.all { it.closed })
    }
}
