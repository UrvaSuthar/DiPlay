package com.shilapi.xcertplay.airplay

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

/** android-8.1: every hard-coded `::` wildcard bind must go through [WildcardBind]. */
class WildcardBindCoverageTest {
    private val directIpv6Bind = Regex("""(InetSocketAddress|ServerSocket|DatagramSocket)\([^)]*InetAddress\.getByName\("::"\)""")

    @Test
    fun noDirectIpv6WildcardBinds() {
        val offenders = File("src/main/java").walkTopDown()
            .filter { it.isFile && it.extension == "kt" && it.name != "WildcardBind.kt" }
            .flatMap { file ->
                file.readLines().mapIndexedNotNull { index, line ->
                    if (directIpv6Bind.containsMatchIn(line)) "${file.name}:${index + 1}" else null
                }
            }
            .toList()
        assertEquals(emptyList<String>(), offenders)
    }
}
