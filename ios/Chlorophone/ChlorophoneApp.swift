import SwiftUI

@main
struct ChlorophoneApp: App {
    @StateObject private var model = AppModel()
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(model)
                .preferredColorScheme(.dark)
                .tint(Theme.lime)
        }
        .onChange(of: scenePhase) { phase in
            // A held push-to-talk cannot survive the app being backgrounded
            // (the touch is gone), so make sure we are not left transmitting.
            if phase == .background {
                model.setTransmitting(false)
            }
        }
    }
}

struct RootView: View {
    @EnvironmentObject private var model: AppModel

    var body: some View {
        ZStack {
            Theme.bg.ignoresSafeArea()

            switch model.phase {
            case .disconnected:
                BookmarksView()
            case .connecting, .reconnecting, .connected:
                ConnectedView()
            }
        }
    }
}
