import SwiftUI

/// The in-call screen: channel tree on top, talk controls at the bottom.
struct ConnectedView: View {
    @EnvironmentObject private var model: AppModel
    @State private var showingChat = false
    @State private var showingSettings = false
    @State private var directChatTarget: ClientRow?

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                if let banner = model.banner {
                    Banner(text: banner, tone: .warning)
                }
                if let error = model.lastError {
                    Banner(text: error, tone: .danger) { model.lastError = nil }
                }
                channelList
                controlBar
            }
            .background(Theme.bg)
            .navigationTitle(model.serverName.isEmpty ? "Chlorophone" : model.serverName)
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
                        model.disconnect()
                    } label: {
                        Image(systemName: "xmark.circle")
                    }
                    .foregroundStyle(Theme.danger)
                }
            }
            .sheet(isPresented: $showingChat) {
                ChatView(title: "Channel chat", target: nil)
            }
            .sheet(item: $directChatTarget) { row in
                ChatView(title: row.name, target: row.clientId)
            }
            .sheet(isPresented: $showingSettings) {
                SettingsView()
            }
        }
    }

    // MARK: - Channel tree

    private var channelList: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 2) {
                ForEach(flatRows, id: \.channel.id) { row in
                    VStack(alignment: .leading, spacing: 0) {
                        channelRow(row)
                        ForEach(row.clients) { client in
                            clientRow(client)
                        }
                    }
                }
                if flatRows.isEmpty {
                    Text("Waiting for the channel list…")
                        .font(.footnote)
                        .foregroundStyle(Theme.textFaint)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                }
            }
            .padding(.vertical, 8)
        }
        .frame(maxHeight: .infinity, alignment: .top)
    }

    private var flatRows: [ChannelNode.Flat] {
        model.tree.flatMap { $0.flattened }
    }

    private func channelRow(_ row: ChannelNode.Flat) -> some View {
        Button {
            model.joinChannel(row.channel.id)
        } label: {
            HStack(spacing: 8) {
                Image(systemName: row.channel.hasPassword ? "lock.fill" : "number")
                    .font(.caption)
                    .foregroundStyle(Theme.textFaint)
                Text(row.channel.name)
                    .font(.subheadline.bold())
                    .foregroundStyle(Theme.textNormal)
                if !row.channel.clients.isEmpty {
                    Text("\(row.channel.clients.count)")
                        .font(.caption)
                        .foregroundStyle(Theme.textFaint)
                }
            }
            .padding(.leading, CGFloat(16 + row.depth * 18))
            .padding(.trailing, 16)
            .padding(.vertical, 9)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Theme.surfaceDark)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private func clientRow(_ client: ClientRow) -> some View {
        HStack(spacing: 8) {
            Circle()
                .fill(Theme.avatarTint(for: client.name))
                .frame(width: 8, height: 8)
            Text(client.name)
                .font(.footnote)
                .foregroundStyle(client.isSelf ? Theme.textNormal : Theme.textMuted)
                .italic(client.presence.away)
            if client.isSelf {
                Text("(you)")
                    .font(.caption2)
                    .foregroundStyle(Theme.textFaint)
            }
            if client.presence.micMuted {
                Image(systemName: "mic.slash.fill")
                    .font(.caption2)
                    .foregroundStyle(Theme.danger)
            }
            if client.presence.outputMuted {
                Image(systemName: "speaker.slash.fill")
                    .font(.caption2)
                    .foregroundStyle(Theme.danger)
            }
            Spacer()
            if client.presence.talking {
                Image(systemName: "waveform")
                    .font(.caption)
                    .foregroundStyle(Theme.speaking)
            }
        }
        .padding(.leading, 38)
        .padding(.trailing, 16)
        .padding(.vertical, 5)
        .background(
            client.presence.talking
                ? (client.isSelf ? Theme.rowYouTalking : Theme.rowTalking)
                : Color.clear
        )
        .contentShape(Rectangle())
        .onTapGesture {
            directChatTarget = client
        }
    }

    // MARK: - Controls

    private var controlBar: some View {
        HStack(spacing: 18) {
            Button {
                model.setMuted(!model.inputMuted)
            } label: {
                Image(systemName: model.inputMuted ? "mic.slash.fill" : "mic.fill")
                    .font(.title3)
                    .foregroundStyle(model.inputMuted ? Theme.danger : Theme.textNormal)
                    .frame(width: 44, height: 44)
                    .background(Theme.surfaceHigh)
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)

            Button {
                model.setDeafened(!model.outputDeafened)
            } label: {
                Image(systemName: model.outputDeafened ? "speaker.slash.fill" : "speaker.wave.2.fill")
                    .font(.title3)
                    .foregroundStyle(model.outputDeafened ? Theme.danger : Theme.textNormal)
                    .frame(width: 44, height: 44)
                    .background(Theme.surfaceHigh)
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)

            talkButton

            Button {
                showingChat = true
            } label: {
                Image(systemName: "bubble.left.and.bubble.right.fill")
                    .font(.title3)
                    .foregroundStyle(Theme.textNormal)
                    .frame(width: 44, height: 44)
                    .background(Theme.surfaceHigh)
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 14)
        .background(Theme.surfaceDark)
    }

    private var talkButton: some View {
        Group {
            if model.pttMode == .pushToTalk {
                ZStack {
                    Circle()
                        .fill(model.transmitting ? Theme.speaking : Theme.lime)
                        .frame(width: 82, height: 82)
                    Text(model.transmitting ? "LIVE" : "TALK")
                        .font(.headline.bold())
                        .foregroundStyle(Color(hex: 0x101D25))
                }
                .contentShape(Circle())
                .onLongPressGesture(
                    minimumDuration: 0.08,
                    maximumDistance: 1000,
                    pressing: { pressed in model.setTransmitting(pressed) },
                    perform: {}
                )
            } else {
                Button {
                    model.setMuted(!model.inputMuted)
                } label: {
                    ZStack {
                        Circle()
                            .fill(model.inputMuted ? Theme.surfaceHigh : Theme.speaking)
                            .frame(width: 82, height: 82)
                        Image(systemName: model.inputMuted ? "mic.slash.fill" : "mic.fill")
                            .font(.title)
                            .foregroundStyle(model.inputMuted ? Theme.textNormal : Color(hex: 0x101D25))
                    }
                }
                .buttonStyle(.plain)
            }
        }
    }
}
