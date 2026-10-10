package com.plnt.client.core

import android.content.Context
import android.util.Log

/**
 * Registers the app's Context with plnt-core's native side (PHA-3289).
 *
 * tsclientlib resolves server hostnames with hickory-resolver, which on Android
 * asks `ndk-context` for a Context to read the phone's DNS servers. Without
 * this call the native side fails with "android context was not initialized"
 * and every connect by hostname fails (IP addresses still worked, which is why
 * it went unnoticed). Idempotent; must run before the first connect.
 */
object AndroidContext {
    @Volatile private var done = false

    fun ensure(context: Context) {
        if (done) return
        synchronized(this) {
            if (done) return
            runCatching {
                // JNA loads the same .so for UniFFI; System.loadLibrary is what
                // makes the JVM bind this JNI method. Loading twice is a no-op.
                System.loadLibrary("plnt_core")
                init(context.applicationContext)
                done = true
            }.onFailure { Log.w("AndroidContext", "native context init failed; hostname DNS may fail", it) }
        }
    }

    @JvmStatic
    private external fun init(context: Context)
}
