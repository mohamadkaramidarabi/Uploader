# Uploader

Kotlin Multiplatform library for chunked file uploads to Drive, with pause/resume support, Room persistence on Android/JVM, and IndexedDB persistence on Web.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.mohamadkaramidarabi/uploader-api)](https://central.sonatype.com/search?q=io.github.mohamadkaramidarabi)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Repository: [github.com/mohamadkaramidarabi/uploader](https://github.com/mohamadkaramidarabi/uploader)

## Library functionality

- Chunked uploads for large files with configurable part sizes from the server.
- Upload lifecycle management: queue, start, progress tracking, pause, resume, retry, and cancel.
- Cross-platform file chunk reading (`android`, `jvm`, `js`, `wasmJs`) through a shared uploader API.
- Persistent upload state:
  - Room-backed cache on Android/JVM.
  - IndexedDB-backed cache on Web, including stored file blobs for resume after refresh.
- Structured upload model with links/parts, retry counters, and final multipart completion request support.

## Published libraries

These modules are published to Maven Central under `io.github.mohamadkaramidarabi`:

| Artifact | Gradle module | Description |
|----------|---------------|-------------|
| `uploader-api` | `:api` | Core uploader API and engine |
| `uploader-common` | `:common` | Shared models |
| `uploader-cache-api` | `:cache:cache-api` | Cache entities and DAO interfaces |
| `uploader-database` | `:cache:database` | Room database implementation |

Sample **applications** in this repo (`androidApp`, `desktopApp`, `webApp`, `iosApp`) and the demo UI module (`composeApp`) are not published.

### Maven dependency

In your Kotlin Multiplatform `commonMain` (or platform source set):

```kotlin
implementation("io.github.mohamadkaramidarabi:uploader-api:0.1.8")
```

Initialize the uploader with your upload callbacks and a platform file reader context:

```kotlin
val uploader = IUploader.init(
    startUpload = { size, metaData -> /* ... */ },
    putChunk = { url, data, size -> /* ... */ },
    completeUpload = { request -> /* ... */ },
    cancelUpload = { uploadInfo -> /* ... */ },
    fileReaderContext = platformContext,
)
```

## Project structure

| Module | Role |
|--------|------|
| [`api`](./api), [`common`](./common), [`cache`](./cache) | **Published** shared libraries |
| [`composeApp`](./composeApp/src) | Demo UI (Compose Multiplatform), not published |
| [`androidApp`](./androidApp) | Android sample application |
| [`desktopApp`](./desktopApp) | Desktop (JVM) sample application |
| [`webApp`](./webApp) | Web (JS + Wasm) sample application |
| [`iosApp`](./iosApp/iosApp) | iOS sample application (SwiftUI shell) |

Platform-specific entry points live in the app modules. `composeApp` holds shared UI plus `expect`/`actual` code in target source sets (`androidMain`, `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`, `webMain`).

## Build and run sample apps

### Android

```shell
# macOS/Linux
./gradlew :androidApp:assembleDebug

# Windows
.\gradlew.bat :androidApp:assembleDebug
```

### Desktop (JVM)

```shell
# macOS/Linux
./gradlew :desktopApp:run

# Windows
.\gradlew.bat :desktopApp:run
```

### Web

Wasm target (recommended, modern browsers):

```shell
# macOS/Linux
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Windows
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

JS target (broader browser support):

```shell
# macOS/Linux
./gradlew :webApp:jsBrowserDevelopmentRun

# Windows
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
```

### iOS

Use the **iosApp** run configuration in the IDE, or open [`iosApp`](./iosApp) in Xcode and run from there.

## Publishing

### Local publish (maintainers)

```shell
./gradlew publishAllModules
```

Requires Sonatype and GPG credentials in `~/.gradle/gradle.properties` or as environment variables:

- `ORG_GRADLE_PROJECT_mavenCentralUsername`
- `ORG_GRADLE_PROJECT_mavenCentralPassword`
- `ORG_GRADLE_PROJECT_signingInMemoryKey`
- `ORG_GRADLE_PROJECT_signingInMemoryKeyId`
- `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`

Override the version locally:

```shell
./gradlew publishAllModules -Pversion=0.1.9
```

### CI publish (GitHub Actions)

Publishing runs automatically when a version tag is pushed (`v*`, e.g. `v0.1.9`):

1. Merge changes to `main`
2. `git tag vX.Y.Z`
3. `git push origin vX.Y.Z`

The workflow [`.github/workflows/publish.yml`](./.github/workflows/publish.yml) runs `publishAllModules` and releases to Maven Central.

#### Required GitHub secrets

Add these under **Settings → Secrets and variables → Actions**:

| Secret | Purpose |
|--------|---------|
| `MAVEN_CENTRAL_USERNAME` | Sonatype Central username / token |
| `MAVEN_CENTRAL_PASSWORD` | Sonatype Central password / token |
| `SIGNING_KEY` | ASCII-armored GPG private key |
| `SIGNING_KEY_ID` | GPG key id (8 characters) |
| `SIGNING_KEY_PASSWORD` | GPG key passphrase |

#### First-time GitHub setup

```bash
git remote add origin https://github.com/mohamadkaramidarabi/uploader.git
git push -u origin main
git tag v0.1.9
git push origin v0.1.9
```

Do not re-publish a version that already exists on Maven Central.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
and [Kotlin/Wasm](https://kotl.in/wasm/).
