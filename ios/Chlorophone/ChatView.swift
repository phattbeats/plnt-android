import SwiftUI

/// Channel chat and private chat. Server-wide chat is out of scope for v1,
/// same as on Android — the core only speaks channel and client-to-client.
struct ChatView: View {
    @EnvironmentObject private var model: AppModel
    @Environment(\.dismiss) private var dismiss

    let title: String
    /// nil = channel chat; a client id = private chat with that client.
    let target: UInt64?

    @State private var draft: String = ""

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                if model.chatMessages.isEmpty {
                    Spacer()
                    Text("No messages yet.")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                    Spacer()
                } else {
                    ScrollViewReader { proxy in
                        ScrollView {
                            LazyVStack(alignment: .leading, spacing: 8) {
                                ForEach(model.chatMessages) { message in
                                    messageRow(message)
                                        .id(message.id)
                                }
                            }
                            .padding(14)
                        }
                        .onChange(of: model.chatMessages.count) { _ in
                            if let last = model.chatMessages.last {
                                withAnimation { proxy.scrollTo(last.id, anchor: .bottom) }
                            }
                        }
                    }
                }

                Divider().background(Theme.divider)

                HStack(spacing: 10) {
                    TextField("Message", text: $draft)
                        .textFieldStyle(.plain)
                        .foregroundStyle(Theme.textNormal)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 9)
                        .background(Theme.surfaceHigh)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                        .submitLabel(.send)
                        .onSubmit(send)
                    Button(action: send) {
                        Image(systemName: "arrow.up.circle.fill")
                            .font(.title2)
                            .foregroundStyle(draft.trimmingCharacters(in: .whitespaces).isEmpty ? Theme.textFaint : Theme.lime)
                    }
                    .buttonStyle(.plain)
                    .disabled(draft.trimmingCharacters(in: .whitespaces).isEmpty)
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Theme.surfaceDark)
            }
            .background(Theme.bg)
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                        .foregroundStyle(Theme.lime)
                }
            }
        }
    }

    private func messageRow(_ message: ChatMessage) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            HStack(spacing: 6) {
                Text(message.isSelf ? "You" : message.fromName)
                    .font(.caption.bold())
                    .foregroundStyle(message.isSelf ? Theme.lime : Theme.avatarTint(for: message.fromName))
                if message.isDirect {
                    Text("private")
                        .font(.caption2)
                        .foregroundStyle(Theme.textFaint)
                }
            }
            Text(message.text)
                .font(.subheadline)
                .foregroundStyle(Theme.textNormal)
                .padding(.horizontal, 10)
                .padding(.vertical, 7)
                .background(Theme.surfaceRaised)
                .clipShape(RoundedRectangle(cornerRadius: 10))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private func send() {
        let text = draft.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }
        model.sendChat(text: text, to: target)
        draft = ""
    }
}
