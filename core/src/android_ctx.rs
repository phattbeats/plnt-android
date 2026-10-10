//! Hands the JavaVM and the application `Context` to `ndk-context` (PHA-3289).
//!
//! hickory-resolver (tsclientlib's DNS) reads the phone's DNS servers on
//! Android through `ndk_context::android_context()`, which panics with
//! "android context was not initialized" unless someone registered the VM and
//! a Context first. Nothing did, so connecting by *hostname* failed while IP
//! addresses worked. The Kotlin side calls this once, before any connect, via
//! `com.plnt.client.core.AndroidContext.init(context)`.
//!
//! Raw `jni-sys` rather than the `jni` crate: two calls, no extra API surface.

use std::ffi::c_void;
use std::sync::Once;

static INIT: Once = Once::new();

/// JNI entry point for `AndroidContext.init(Context)` (a `@JvmStatic` method).
///
/// # Safety
/// Called by the JVM with a valid `env` and a live `Context` local reference.
#[no_mangle]
pub unsafe extern "system" fn Java_com_plnt_client_core_AndroidContext_init(
    env: *mut jni_sys::JNIEnv,
    _class: jni_sys::jclass,
    context: jni_sys::jobject,
) {
    if env.is_null() || context.is_null() {
        return;
    }
    // ndk-context may only be initialized once per process.
    INIT.call_once(|| unsafe {
        let interface: *const jni_sys::JNINativeInterface_ = *env;
        let mut vm: *mut jni_sys::JavaVM = std::ptr::null_mut();
        if ((*interface).v1_1.GetJavaVM)(env, &mut vm) != 0 || vm.is_null() {
            log::warn!("android_ctx: GetJavaVM failed; DNS by hostname will not work");
            return;
        }
        // A global ref: the Context must outlive this JNI call. Never freed,
        // by design: it is the application context and lives as long as we do.
        let global = ((*interface).v1_1.NewGlobalRef)(env, context);
        if global.is_null() {
            log::warn!("android_ctx: NewGlobalRef failed; DNS by hostname will not work");
            return;
        }
        ndk_context::initialize_android_context(vm as *mut c_void, global as *mut c_void);
    });
}
