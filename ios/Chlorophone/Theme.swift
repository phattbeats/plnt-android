import SwiftUI

/// Palette, ported from the Android app's `ui/theme/Palette.kt` so both
/// clients read as the same product: TeamSpeak blue on Discord-style greys,
/// with the Chlorophone lime as the app accent.
enum Theme {
    static let bg = Color(hex: 0x1A1B1E)
    static let surfaceDark = Color(hex: 0x232428)
    static let surfaceRaised = Color(hex: 0x2B2D31)
    static let surfaceHigh = Color(hex: 0x35373C)
    static let divider = Color(hex: 0x3F4147)

    static let textNormal = Color(hex: 0xF2F3F5)
    static let textMuted = Color(hex: 0xB5BAC1)
    static let textFaint = Color(hex: 0x80848E)

    static let brand = Color(hex: 0x5A78C8)
    static let lime = Color(hex: 0xA9F46A)
    static let sprout = Color(hex: 0x50CFA3)
    static let speaking = Color(hex: 0x23A55A)
    static let danger = Color(hex: 0xF23F43)
    static let warning = Color(hex: 0xF0B232)

    static let rowTalking = Color(hex: 0x1F2A23)
    static let rowYouTalking = Color(hex: 0x232A3A)

    /// Avatar tints, picked by name hash so a person keeps their colour
    /// between sessions.
    static let avatarTints: [Color] = [
        Color(hex: 0x5865F2),
        Color(hex: 0x3BA55C),
        Color(hex: 0xED4245),
        Color(hex: 0xC27C0E),
        Color(hex: 0xEB459E),
        Color(hex: 0x4B69B6),
    ]

    static func avatarTint(for name: String) -> Color {
        var hash: UInt64 = 5381
        for byte in name.utf8 {
            hash = (hash &* 33) &+ UInt64(byte)
        }
        return avatarTints[Int(hash % UInt64(avatarTints.count))]
    }
}

extension Color {
    init(hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255.0,
            green: Double((hex >> 8) & 0xFF) / 255.0,
            blue: Double(hex & 0xFF) / 255.0,
            opacity: 1.0
        )
    }
}

/// A one-line dismissible message strip. Errors and "reconnecting" banners use
/// it — never a modal alert, because a call that hiccups should not steal the
/// screen.
struct Banner: View {
    let text: String
    var tone: Tone = .info
    var onDismiss: (() -> Void)?

    enum Tone {
        case info, warning, danger

        var color: Color {
            switch self {
            case .info: return Theme.brand
            case .warning: return Theme.warning
            case .danger: return Theme.danger
            }
        }
    }

    var body: some View {
        HStack(spacing: 10) {
            Circle()
                .fill(tone.color)
                .frame(width: 8, height: 8)
            Text(text)
                .font(.footnote)
                .foregroundStyle(Theme.textNormal)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let onDismiss {
                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .font(.caption.bold())
                        .foregroundStyle(Theme.textMuted)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 10)
        .background(Theme.surfaceRaised)
    }
}
