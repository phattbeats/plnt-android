package com.plnt.client.security

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/**
 * PHA-4856 (WS8): every encrypted store and every diagnostics file the app
 * writes is excluded from cloud backup, device-to-device transfer and the
 * pre-API-31 full backup. The store names are read out of the source, so a
 * new encrypted store that is not added to the rules fails here.
 */
class BackupRulesTest {
    // Unit tests run with the module directory (app/app) as working directory.
    private val main = File("src/main")

    private fun excludes(file: String, section: String?): Set<Pair<String, String>> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(main, "res/xml/$file"))
        val scope = if (section == null) doc.documentElement
        else doc.getElementsByTagName(section).item(0) as Element? ?: error("$file has no <$section>")
        val nodes = scope.getElementsByTagName("exclude")
        return (0 until nodes.length).map { i ->
            val e = nodes.item(i) as Element
            e.getAttribute("domain") to e.getAttribute("path")
        }.toSet()
    }

    private val ruleSets by lazy {
        mapOf(
            "data_extraction_rules.xml <cloud-backup>" to excludes("data_extraction_rules.xml", "cloud-backup"),
            "data_extraction_rules.xml <device-transfer>" to excludes("data_extraction_rules.xml", "device-transfer"),
            "backup_rules.xml" to excludes("backup_rules.xml", null),
        )
    }

    /** Names passed to openEncryptedPrefs, directly or through a PREFS_NAME constant. */
    private fun encryptedStoreNames(): Set<String> {
        val src = main.resolve("kotlin").walkTopDown().filter { it.extension == "kt" }.map { it.readText() }.toList()
        val direct = Regex("""openEncryptedPrefs\([^,]+,\s*"([^"]+)"""")
        val viaConstant = Regex("""openEncryptedPrefs\([^,]+,\s*PREFS_NAME""")
        val constant = Regex("""PREFS_NAME\s*=\s*"([^"]+)"""")
        return src.flatMap { text ->
            direct.findAll(text).map { it.groupValues[1] }.toList() +
                (if (viaConstant.containsMatchIn(text)) constant.findAll(text).map { it.groupValues[1] }.toList() else emptyList())
        }.toSet()
    }

    @Test fun findsTheKnownEncryptedStores() {
        val names = encryptedStoreNames()
        for (n in listOf("plnt_identity", "plnt_session", "plnt_bookmark_secrets")) {
            assertTrue("scan missed $n (found $names)", n in names)
        }
    }

    @Test fun everyEncryptedStoreIsExcludedEverywhere() {
        for (name in encryptedStoreNames()) {
            for ((where, rules) in ruleSets) {
                assertTrue("$name.xml is not excluded in $where", "sharedpref" to "$name.xml" in rules)
            }
        }
    }

    @Test fun diagnosticsStayOnThePhone() {
        for ((where, rules) in ruleSets) {
            assertTrue("diag log not excluded in $where", "file" to "plnt-diag.log" in rules)
            assertTrue("diag opt-in not excluded in $where", "sharedpref" to "plnt_diag.xml" in rules)
        }
    }

    @Test fun manifestPointsAtBothRuleFiles() {
        val manifest = File(main, "AndroidManifest.xml").readText()
        assertTrue(manifest.contains("""android:dataExtractionRules="@xml/data_extraction_rules""""))
        assertTrue(manifest.contains("""android:fullBackupContent="@xml/backup_rules""""))
    }
}
