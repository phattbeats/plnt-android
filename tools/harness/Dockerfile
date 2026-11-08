# chlorophone-android-harness — build/test runner for plnt-android on PHATT-RAID (PHA-5006).
#
# Bakes in the same toolchain .github/workflows/build.yml installs, so
# ./build.sh finds everything present and downloads nothing. Pins mirror
# build.sh; bump them together.
FROM gradle:8.10.2-jdk17 AS gradle

FROM ubuntu:24.04

ARG CMDLINE_TOOLS_BUILD=11076708
ARG NDK_VERSION=27.0.12077973
ARG CARGO_NDK_VERSION=4.1.0

ENV DEBIAN_FRONTEND=noninteractive \
    JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
    ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    RUSTUP_HOME=/opt/rustup \
    CARGO_HOME=/opt/cargo \
    GRADLE_USER_HOME=/cache/gradle \
    NDK_VERSION=${NDK_VERSION} \
    NVIDIA_VISIBLE_DEVICES=all \
    NVIDIA_DRIVER_CAPABILITIES=all
# audiopus_sys builds Opus with CMake, whose Android platform module finds the
# NDK only via these (the GitHub runner image exports them; we must too).
ENV ANDROID_NDK=/opt/android-sdk/ndk/${NDK_VERSION} \
    ANDROID_NDK_HOME=/opt/android-sdk/ndk/${NDK_VERSION} \
    ANDROID_NDK_ROOT=/opt/android-sdk/ndk/${NDK_VERSION}
ENV PATH=/opt/cargo/bin:/opt/gradle/bin:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/opt/android-sdk/emulator:$PATH

RUN apt-get update -qq && apt-get install -yq --no-install-recommends \
      openjdk-17-jdk-headless ca-certificates curl git unzip file python3 \
      build-essential python3-pip pkg-config libopus-dev libasound2-dev \
      libegl1 libgl1 libglvnd0 iproute2 \
    && rm -rf /var/lib/apt/lists/*

# CMake 3.31 to match the GitHub runner: Ubuntu's 3.28 rejects
# CMAKE_SYSTEM_PROCESSOR=aarch64 for Android, which audiopus_sys passes.
# From PyPI because Kitware's tarballs live on GitHub releases (503s from RAID).
RUN pip install --no-cache-dir --break-system-packages cmake==3.31.6 && cmake --version

# Gradle comes from the official image: its GitHub-hosted zip 504s often.
COPY --from=gradle /opt/gradle /opt/gradle

RUN mkdir -p $ANDROID_HOME/cmdline-tools && cd $ANDROID_HOME/cmdline-tools \
    && curl -fsSL --retry 5 --retry-all-errors -o t.zip "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_BUILD}_latest.zip" \
    && unzip -q t.zip && mv cmdline-tools latest && rm t.zip \
    && (yes | sdkmanager --licenses >/dev/null || true) \
    && sdkmanager --install "platform-tools" "ndk;${NDK_VERSION}" \
         "platforms;android-34" "platforms;android-35" \
         "build-tools;34.0.0" "build-tools;35.0.0" >/dev/null

RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --profile minimal --default-toolchain stable \
    && rustup target add aarch64-linux-android x86_64-linux-android \
    && cargo install --locked "cargo-ndk@${CARGO_NDK_VERSION}" \
    && rm -rf /opt/cargo/registry /opt/cargo/git

# The nvidia runtime injects libEGL_nvidia but not its glvnd vendor file;
# without it EGL resolves to Mesa and `emulator -gpu host` gets no GPU.
COPY 10_nvidia.json /usr/share/glvnd/egl_vendor.d/10_nvidia.json
COPY entrypoint.sh /usr/local/bin/harness-entrypoint
WORKDIR /work
# adb server on 5037, all interfaces inside the container; the host side
# publishes it on the LAN address only (see run.sh).
EXPOSE 5037
ENTRYPOINT ["/usr/local/bin/harness-entrypoint"]
