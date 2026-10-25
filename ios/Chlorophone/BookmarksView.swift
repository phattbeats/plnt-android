import SwiftUI

/// The "servers" screen — what the Android app calls the bookmarks list.
/// One row per saved TeamSpeak server, with a connect button.
struct BookmarksView: View {
    @EnvironmentObject private var model: AppModel
    @State private var editorDraft: Bookmark?
    @State private var showingSettings = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                if let error = model.lastError {
                    Banner(text: error, tone: .danger) { model.lastError = nil }
                }
                if model.bookmarks.isEmpty {
                    emptyState
                } else {
                    serverList
                }
                Spacer(minLength: 0)
            }
            .background(Theme.bg)
            .navigationTitle("Chlorophone")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        showingSettings = true
                    } label: {
                        Image(systemName: "gearshape")
                    }
                    .foregroundStyle(Theme.textMuted)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        editorDraft = Bookmark(
                            label: "",
                            address: "",
                            port: 9987,
                            nickname: ""
                        )
                    } label: {
                        Image(systemName: "plus")
                    }
                    .foregroundStyle(Theme.lime)
                }
            }
            .sheet(item: $editorDraft) { draft in
                ServerEditor(bookmark: draft) { saved in
                    model.save(bookmark: saved)
                }
            }
            .sheet(isPresented: $showingSettings) {
                SettingsView()
            }
        }
    }

    private var serverList: some View {
        ScrollView {
            VStack(spacing: 10) {
                ForEach(model.bookmarks) { bookmark in
                    serverRow(bookmark)
                }
            }
            .padding(.horizontal, 14)
            .padding(.top, 10)
        }
    }

    private func serverRow(_ bookmark: Bookmark) -> some View {
        HStack(spacing: 12) {
            Button {
                editorDraft = bookmark
            } label: {
                VStack(alignment: .leading, spacing: 3) {
                    Text(bookmark.label.isEmpty ? bookmark.address : bookmark.label)
                        .font(.headline)
                        .foregroundStyle(Theme.textNormal)
                    Text("\(bookmark.address):\(bookmark.port) · \(bookmark.nickname)")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            Button {
                model.connect(to: bookmark)
            } label: {
                Text("Connect")
                    .font(.subheadline.bold())
                    .foregroundStyle(Color(hex: 0x101D25))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Theme.lime)
                    .clipShape(Capsule())
            }
            .buttonStyle(.plain)
            .disabled(model.phase != .disconnected)
        }
        .padding(14)
        .background(Theme.surfaceDark)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var emptyState: some View {
        VStack(spacing: 12) {
            Spacer()
            Image(systemName: "waveform")
                .font(.system(size: 44))
                .foregroundStyle(Theme.lime)
            Text("No servers yet")
                .font(.title3.bold())
                .foregroundStyle(Theme.textNormal)
            Text("Add a TeamSpeak server to get started.\nEverything stays on this phone.")
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .foregroundStyle(Theme.textMuted)
            Button {
                editorDraft = Bookmark(
                    label: "",
                    address: "",
                    port: 9987,
                    nickname: ""
                )
            } label: {
                Text("Add a server")
                    .font(.subheadline.bold())
                    .foregroundStyle(Color(hex: 0x101D25))
                    .padding(.horizontal, 18)
                    .padding(.vertical, 10)
                    .background(Theme.lime)
                    .clipShape(Capsule())
            }
            .buttonStyle(.plain)
            Spacer()
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 40)
    }
}

/// Add / edit one saved server.
struct ServerEditor: View {
    @Environment(\.dismiss) private var dismiss
    @State var bookmark: Bookmark
    let onSave: (Bookmark) -> Void

    var body: some View {
        NavigationStack {
            Form {
                Section("Name") {
                    TextField("Label", text: $bookmark.label)
                }
                Section("Server") {
                    TextField("Address (host or IP)", text: $bookmark.address)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                        .keyboardType(.URL)
                    TextField("Port", value: $bookmark.port, format: .number)
                        .keyboardType(.numberPad)
                }
                Section("You") {
                    TextField("Nickname", text: $bookmark.nickname)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                    SecureField("Server password (optional)", text: Binding(
                        get: { bookmark.serverPassword ?? "" },
                        set: { bookmark.serverPassword = $0.isEmpty ? nil : $0 }
                    ))
                }
            }
            .scrollContentBackground(.hidden)
            .background(Theme.bg)
            .navigationTitle(bookmark.label.isEmpty ? "Add server" : "Edit server")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                        .foregroundStyle(Theme.textMuted)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Save") {
                        onSave(bookmark)
                        dismiss()
                    }
                    .font(.body.bold())
                    .foregroundStyle(Theme.lime)
                    .disabled(!isValid)
                }
            }
        }
    }

    private var isValid: Bool {
        !bookmark.address.trimmingCharacters(in: .whitespaces).isEmpty
            && !bookmark.nickname.trimmingCharacters(in: .whitespaces).isEmpty
            && bookmark.port > 0 && bookmark.port <= 65535
    }
}
