# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Koler is a GPL-3.0 Android dialer (Kotlin, MVVM, Hilt). Published on F-Droid, Google Play and GitHub releases; store metadata lives in `fastlane/metadata/android/`. Translations come from Weblate — don't hand-edit non-English `values-*/strings.xml` unless needed.

## Commands

Gradle wrapper, AGP 7.4, Kotlin 1.8, JDK 18 toolchain (`jvmToolchain(18)`). On Windows use `gradlew.bat`.

```sh
./gradlew :koler:assembleDebug          # build the dialer app
./gradlew :kontacts:assembleDebug       # build the contacts app
./gradlew :koler:installDebug           # install on connected device
./gradlew lint                          # Android lint (release lint checks are disabled)
./gradlew :chooloolib:testDebugUnitTest # JVM unit tests
./gradlew :chooloolib:testDebugUnitTest --tests "com.chooloo.www.chooloolib.ExampleUnitTest"
./gradlew :chooloolib:connectedAndroidTest  # instrumented tests (needs device/emulator)
```

Test coverage is essentially placeholder (`ExampleUnitTest` / `ExampleInstrumentedTest`); verify changes by running the app. Version is bumped manually via `versionCode`/`versionName` in `koler/build.gradle`.

## Modules

- `chooloolib` — Android library holding almost all code: UI, view models, data, DI, the `InCallService`.
- `koler` — the dialer app; thin shell (`KolerApp`, `MainActivity`, its own `SettingsFragment`) over `chooloolib`.
- `kontacts` — a separate contacts app also built on `chooloolib`.

Shared SDK/library versions are in `ext` in the root `build.gradle`.

## Architecture (chooloolib)

Layers, all wired by Hilt (`di/module`):

- **Content resolvers** (`data/contentresolver`) — query Android providers (contacts, phones, raw contacts, call log, phone lookup), created through `ContentResolverFactory`.
- **Repositories** (`data/repository/*`) — interface + `Impl`, expose `Flow`s built on the content resolvers.
- **Interactors** (`interactor/*`) — interface + `Impl` wrappers around system services and app logic (calls, audio, telecom, permissions, preferences, navigation, SIM, theme…). Many extend `BaseInteractor` with listener registration.
- **ViewStates** (`ui/**/XViewState.kt`) — `@HiltViewModel`s extending `BaseViewState` (or `ListViewState` for lists). They emit one-shot UI events via `MutableLiveEvent`/`MutableDataLiveEvent` (`util/LiveEvent.kt`) exposed as read-only `LiveEvent`s, plus `finish/error/message` events from the base.
- **Fragments/Activities** (`ui/**`) — extend `BaseFragment`/`BaseActivity`/`BottomFragment`, use view binding and observe the ViewState. Fragments are created through `FragmentFactory` (`di/factory/fragment`) rather than constructed directly; `koler` and `kontacts` each have their own `FragmentFactory` for app-specific screens.

Convention: new features follow interface + `Impl` + Hilt `@Binds` (see `BindsModule`s), and app modules include the lib's `ActivityModule.BindsModule`.

### Call handling

`api/service/CallService` is the `InCallService`. Telecom calls are wrapped in `data/model/Call` and pushed to `CallsInteractor` (`entryAddCall` / `entryRemoveCall`) and `CallsRepository`; audio state goes to `CallAudiosInteractor`. It launches `ui/call/CallActivity` for outgoing calls or when the incoming-call mode preference is full screen, otherwise `notification/CallNotification` handles it. Hold/swap/merge failures surface as the `Cant*CallException`s in `data/model`.
