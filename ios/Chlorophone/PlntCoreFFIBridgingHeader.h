// Bridging header for the UniFFI FFI layer.
//
// The generated plnt_core.swift calls the C entry points (uniffi_plnt_core_*)
// and uses the C types (RustBuffer, ForeignBytes, RustCallStatus) directly —
// unqualified, which is what a bridging header provides. The include path is
// relative on purpose: putting PlntCoreFFI on the header search path would
// also make the module map discoverable, the generated file would then
// `import plnt_coreFFI` as well, and the same declarations would be visible
// twice.
//
// See ios/README.md and the UniFFI Swift guide ("Including the bridging
// header") for the shape of this.

#pragma once

#include "../PlntCoreFFI/plnt_coreFFI.h"
