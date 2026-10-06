package com.shilapi.xcertplay

import com.shilapi.xcertplay.orchestration.WirelessHotspotMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class WifiDirectAvailabilityTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Test
    @Config(sdk = [27], manifest = Config.NONE)
    fun wifiDirectFallsBackToCarHotspotBelowApi28() {
        AirPlayPersistence.saveWirelessHotspotMode(context, WirelessHotspotMode.WIFI_P2P)
        assertEquals(WirelessHotspotMode.MANUAL, AirPlayPersistence.loadWirelessHotspotMode(context))
    }

    @Test
    @Config(sdk = [28], manifest = Config.NONE)
    fun wifiDirectKeptOnApi28() {
        AirPlayPersistence.saveWirelessHotspotMode(context, WirelessHotspotMode.WIFI_P2P)
        assertEquals(WirelessHotspotMode.WIFI_P2P, AirPlayPersistence.loadWirelessHotspotMode(context))
    }
}
