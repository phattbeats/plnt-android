# CMake toolchain for CMake-built crates (audiopus_sys/Opus) on arm64-v8a.
# The cmake crate doesn't pass ANDROID_ABI, so the NDK toolchain alone
# defaults to armeabi-v7a; pin the ABI here, then defer to the NDK.
set(ANDROID_ABI arm64-v8a)
set(ANDROID_PLATFORM android-34)
include("$ENV{ANDROID_NDK}/build/cmake/android.toolchain.cmake")
