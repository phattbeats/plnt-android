package com.plnt.client.security

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/**
 * PHA-4856 (WS8): the source manifest. CI's tools/audit_apk.py checks the
 * merged manifest of the built debug and release APKs; this catches the same
 * mistakes before a build.
 */
class ManifestAuditTest {
    private val ns = "http://schemas.android.com/apk/res/android"
    private val doc = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        .newDocumentBuilder().parse(File("src/main/AndroidManifest.xml"))
    private val app = doc.getElementsByTagName("application").item(0) as Element

    private fun components(): List<Element> = listOf("activity", "service", "receiver", "provider").flatMap { tag ->
        val n = app.getElementsByTagName(tag)
        (0 until n.length).map { n.item(it) as Element }
    }

    @Test fun neverDebuggableOrCleartext() {
        assertFalse(app.hasAttributeNS(ns, "debuggable"))
        assertFalse(app.getAttributeNS(ns, "usesCleartextTraffic") == "true")
    }

    @Test fun onlyTheLauncherActivityIsExported() {
        val exported = components().filter { it.getAttributeNS(ns, "exported") == "true" }
        assertEquals(listOf(".MainActivity"), exported.map { it.getAttributeNS(ns, "name") })
    }

    @Test fun everyComponentDeclaresExported() {
        for (c in components()) assertTrue("${c.getAttributeNS(ns, "name")} has no android:exported", c.hasAttributeNS(ns, "exported"))
    }

    @Test fun deepLinksAreNotAutoVerified() {
        // The tmspk.gg host is not ours; Android must offer, not silently route.
        val filters = doc.getElementsByTagName("intent-filter")
        for (i in 0 until filters.length) {
            assertFalse((filters.item(i) as Element).getAttributeNS(ns, "autoVerify") == "true")
        }
    }
}
