package com.plnt.client.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/** PHA-4856 (WS8): what reaches the diagnostic trace. */
class DiagRedactorTest {
    private fun redacted(line: String) = DiagRedactor.redact(line)

    @Test fun secretsAndIdentityLeave() {
        val out = redacted("clientinit client_nickname=Brandon client_server_password=hunter2 hwid=abc client_key_offset=12")
        for (s in listOf("Brandon", "hunter2", "abc")) assertFalse("$s survived: $out", s in out)
    }

    @Test fun addressesLeave() {
        val out = redacted("Disconnected reason=Connection to 10.0.0.100:9987 via ts.phatt.vip and [2001:db8::1] timed out")
        for (s in listOf("10.0.0.100", "phatt", "2001:db8")) assertFalse("$s survived: $out", s in out)
    }

    @Test fun keysUrlsAndEmailsLeave() {
        val out = redacted("import MG8DAgeAAgEgAiEAvbXR0F3pkIHmQ9DJN2dgjBk6wy8hjvbF2xXZB5I5Pmk from https://x.example/a?t=1 by me@example.com")
        for (s in listOf("MG8DAge", "x.example", "me@example.com")) assertFalse("$s survived: $out", s in out)
    }

    @Test fun diagnosticContentStays() {
        for (line in listOf(
            "Connected",
            "diag t=15 rtt=42ms loss=0.1",
            "share: quality 1280x720 30fps 2500kbps metered=false",
            "    at com.plnt.client.service.VoiceService.onCreate(VoiceService.kt:308)",
            "java.net.SocketTimeoutException: timeout",
            "  [plnt-diag-upload TIMED_WAITING]",
            "hang-up at 12:34:56",
        )) assertEquals(line, redacted(line))
    }
}
