This is a Kotlin Multiplatform project targeting Android, iOS, Web, and Desktop (JVM).

## Project structure

| Module | Role |
|--------|------|
| [`composeApp`](./composeApp/src) | Shared KMP **library** (UI, networking, platform `actual`s) |
| [`androidApp`](./androidApp) | Android application entry point |
| [`desktopApp`](./desktopApp) | Desktop (JVM) application entry point |
| [`webApp`](./webApp) | Web (JS + Wasm) application entry point |
| [`iosApp`](./iosApp/iosApp) | iOS application (SwiftUI shell; uses `ComposeApp` framework from `composeApp`) |
| [`api`](./api), [`common`](./common), [`cache`](./cache) | Shared libraries |

Platform-specific **entry points** live in the app modules above. `composeApp` keeps `commonMain` plus target source sets (`androidMain`, `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`) for `expect`/`actual` code only.

### Build and Run Android Application

- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

- on macOS/Linux
  ```shell
  ./gradlew :desktopApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :desktopApp:run
  ```

### Build and Run Web Application

- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :webApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :webApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :webApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

Use the **iosApp** run configuration in the IDE, or open the [`iosApp`](./iosApp) directory in Xcode and run from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…
