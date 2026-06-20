# Live-TV (USA IPTV Player)

## Overview
A minimal Android IPTV player using ExoPlayer that loads USA TV channels from the [IPTV-ORG](https://iptv-org.github.io/iptv/) repository. Channels are displayed in a 2-column grid with logos and are playable via HLS/DASH/Smooth Streaming.

## Features
- ExoPlayer-based playback (HLS, DASH, HTTP streams)
- Grid-based channel list with logos
- Loads ~100+ USA channels (ESPN, Fox, NBC, ABC, CBS, etc.)
- Portrait and landscape player orientation
- Progress indicator while loading channels
- Tap to play any channel
- Full-screen player controls

## Building

### Prerequisites
- Android Studio (latest stable) with SDK 34
- Java 11 or higher
- Gradle 8.0+

### Build Debug APK
```bash
git clone https://github.com/swilkey20-byte/Live-TV-.git
cd Live-TV-
git checkout feature/iptv-player
./gradlew assembleDebug
```

The debug APK will be in `app/build/outputs/apk/debug/app-debug.apk`.

### Install on Device/Emulator
```bash
./gradlew installDebug
```

## Channel Source
Channels are fetched from the USA M3U playlist hosted at:
https://iptv-org.github.io/iptv/countries/us.m3u

This list includes 100+ channels including ESPN, Fox Sports, NBC, ABC, CBS, and many others.

## Legal Notice
- This app loads channels from a public, community-maintained M3U playlist.
- Ensure that you only play streams you are legally permitted to access.
- The IPTV-ORG project provides metadata and links only; users are responsible for ensuring they have rights to stream.

## Next Steps
- To build a release APK with your keystore, edit `app/build.gradle` and add signing config.
- To publish to Google Play Store, build an AAB: `./gradlew bundleRelease`
- For GitHub Actions CI/CD, the workflow is in `.github/workflows/build-and-release.yml` (triggered manually or on push).

## Troubleshooting
- **Channels fail to load**: Check internet connection; the playlist URL may be temporarily unavailable.
- **Streams don't play**: Some streams may have geo-blocking or may be offline. Try another channel.
- **Build errors**: Ensure Gradle 8.0+ and Java 11+ are installed.

## License
MIT License (see LICENSE file)
