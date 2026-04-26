# Modo — Agent Orientation

State-based navigation library for Jetpack Compose. UDF architecture: navigation is a tree of `Screen`s and `ContainerScreen`s driven by
`NavigationState` and updated via `dispatch(Action)` on a `NavigationContainer`.

IMPORTANT: When applicable, prefer using android-studio-index MCP tools for code navigation and refactoring.

## Modules

- `modo-compose/` — the library. Core abstractions (`Screen`, `ContainerScreen`, `NavModel`, `NavigationState`, `NavigationContainer`), Compose
  integration (`ComposeRenderer`, `SaveableContent`), Android integration (`ModoScreenAndroidAdapter`, lifecycle, saved state), built-in container
  types (`StackScreen`, `MultiScreen`, `DialogScreen`), and `ScreenModel` infrastructure.
- `sample/` — demo app exercising library features.
- `workshop-app/` — tutorial/workshop codebase used to teach the library.
- `build-logic/` — convention plugins for the Gradle build.
- `Writerside/` — user-facing documentation site (published to GitHub Pages).

## Build & test

```
./gradlew build                          # full build, all modules
./gradlew :modo-compose:test             # library unit tests
./gradlew :modo-compose:testDebugUnitTest
./gradlew :sample:installDebug           # run sample app on a connected device
```

Check `config/` for shared gradle/lint config and `gradle.properties` for JVM/Compose settings. Kotlin code style is enforced; match existing
formatting in the file you're editing.

## Code conventions

- All navigation-facing types live under `com.github.terrakok.modo`.
- `NavigationState` implementations must be `Parcelable` and return every held `Screen` from `getChildScreens()` — this drives cleanup and lifecycle.
- Prefer editing existing files over creating new ones. Only add new files when an abstraction genuinely belongs in its own unit.
- Breaking API changes on public types (`NavigationContainer`, `Screen`, `NavModel`, etc.) require a deliberate decision — surface in a task doc (see
  *Non-trivial work* below) before implementing.

## Non-trivial work

For multi-step tasks (refactors, architectural investigations, work likely to span sessions), we use a task-folder workflow — see the **task-workflow
** skill (`.agents/skills/task-workflow/`). Invoke it via `/task-workflow` (if supported by your agent), or follow the protocol in the skill doc.
Completed tasks produce an ADR under `adr/`.

For trivial fixes, typos, and single-file edits, just do the work — no scaffolding needed.
