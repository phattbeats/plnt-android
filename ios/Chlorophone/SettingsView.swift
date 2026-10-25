import SwiftUI
import UIKit

/// Settings: talk mode, identity, and the About panel (the one place the
/// product name and mark still appear).
struct SettingsView: View {
    @EnvironmentObject private var model: AppModel
    @Environment(\.dismiss) private var dismiss

    @State private var identityPreview: String = ""
    @State private var status: String = ""

    var body: some View {
        NavigationStack {
            Form {
                Section("Talking") {
                    Picker("Mode", selection: Binding(
                        get: { model.pttMode },
                        set: { model.setPttMode($0) }
                    )) {
                        ForEach(PttMode.allCases) { mode in
                            Text(mode.label).tag(mode)
                        }
                    }
                    .pickerStyle(.segmented)
                    Text(model.pttMode == .pushToTalk
                         ? "Hold the talk button to transmit. The microphone stays open in the background so a Bluetooth headset never drops."
                         : "The microphone stays open until you mute it.")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                }

                Section("Audio") {
                    LabeledContent("Output", value: model.audio.currentRouteDescription)
                    LabeledContent("Microphone", value: model.microphoneActive ? "Allowed" : "Blocked")
                }

                Section("Identity") {
                    Text("Your identity is this device's key. Keep it secret — it is what the server knows you by.")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                    if !identityPreview.isEmpty {
                        Text(identityPreview)
                            .font(.caption.monospaced())
                            .foregroundStyle(Theme.textMuted)
                            .lineLimit(3)
                    }
                    Button("Copy identity") {
                        UIPasteboard.general.string = IdentityStore.peek()
                        status = "Identity copied."
                    }
                    Button("Import identity from clipboard") {
                        if let text = UIPasteboard.general.string,
                           IdentityStore.importIdentity(text) {
                            identityPreview = IdentityStore.peek() ?? ""
                            status = "Identity imported."
                        } else {
                            status = "That clipboard does not hold a usable identity."
                        }
                    }
                    Button("Create a new identity", role: .destructive) {
                        identityPreview = IdentityStore.createNew()
                        status = "New identity created. Reconnect to use it."
                    }
                    if !status.isEmpty {
                        Text(status)
                            .font(.footnote)
                            .foregroundStyle(Theme.textFaint)
                    }
                }

                Section("About") {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Chlorophone")
                            .font(.title3.bold())
                            .foregroundStyle(Theme.textNormal)
                        Text("by PHATT TECH")
                            .font(.footnote)
                            .foregroundStyle(Theme.textFaint)
                    }
                    LabeledContent("Core", value: model.coreVersionString)
                    Text("Voice-only TeamSpeak client. The server never sees your identity — everything is on this phone.")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                }
            }
            .scrollContentBackground(.hidden)
            .background(Theme.bg)
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                        .foregroundStyle(Theme.lime)
                }
            }
            .onAppear {
                identityPreview = IdentityStore.peek() ?? ""
            }
        }
    }
}
