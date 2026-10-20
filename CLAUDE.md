# Tuner (音叉 onsa), banjo fork

@AGENTS.md

The parent folder's `../CLAUDE.md` has the cross-app rules.

## Build, test, phone

- `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug`. It has the largest test suite of the
  apps (`app/src/test/java/com/wanderwildwood/onsa`), including `BanjoTuningsTest`.
  `androidTest` needs a device: `:app:connectedDebugAndroidTest`.
- Every build installs as `com.wanderwildwood.onsa.tuner`, beside Wander's Tuner. The phone has the
  release build (key in `signing/`): raise `versionCode`, `:app:assembleRelease`, then
  `adb -d install -r app\build\outputs\apk\release\app-release.apk`. `MusicalNoteTest.testMusicalScaleIndexing`
  fails on upstream's code (not the banjo work).
- AGP 9.1 / Gradle 9.4 / Kotlin 2.3: the newest toolchain here apart from Music Box.

## Conventions

- Fork of wanderwildwood/onsa, which is itself Michael Moessner's Tuner. Pitch detection,
  temperaments and note naming are upstream's: change only the E Ink presentation and the banjo
  addition unless asked.
- Licence is **GPL-3.0-or-later**, not the GPL-3.0-only the other apps use.
- The banjo addition is meant to be offered upstream (see TODO #3), so keep it in reviewable,
  self-contained commits.
- Translations live in `res/values-*`. A new string goes in `values/strings.xml`; leave the
  translations to upstream.
- `fastlane/metadata` is store text and `gonville/` holds the font tooling (Python). Neither is
  part of the build.

## Cross-app

- None today. A natural future link is Chord Sheets' `#banjo/<tuning>` tag to a banjo tuning
  here.
