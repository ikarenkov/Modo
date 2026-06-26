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

## Commit messages

Keep them concise: state what was done. Add a line of detail only when *why* isn't obvious from the change itself — skip the explanation for self-evident edits.

## Releases & changelogs

Per-release notes live at `changelogs/<version>.md` (e.g. `changelogs/0.12.0.md`). The `GitHub Release` workflow expects this exact path; a missing file fails the release.

**Append entries as you work, not at release time.** When you make a user-visible change — new/changed public API, deprecation, behavior change, notable bug fix, sample-app feature — add a bullet to the in-progress changelog in the same task. Defer-and-batch produces "what changed since last release? let me grep git log" archaeology; we explicitly avoid that.

The in-progress changelog is `changelogs/<version>.md` where `<version>` is whatever `modo = "<version>"` currently is in `gradle/libs.versions.toml`. If that file doesn't exist yet, create it. If that version is already released (file exists *and* the version is on Maven Central), the change belongs to the *next* release — bump `modo` in `libs.versions.toml` and create a fresh changelog. Match the section structure (`## Architecture refactor`, `## API changes`, `## Deprecations`, `## Sample app changes`, `## Test changes`, `## Repo meta`) of the previous release file; omit sections that don't apply.

Do **not** include a trailing `## What's Changed` / `**Full Changelog**` block in `changelogs/<version>.md` — the `GitHub Release` workflow appends GitHub's auto-generated equivalent. Including one in the file produces a duplicate on the release page.

To cut a release: ensure `changelogs/<version>.md` is complete, then run the `Publish` and `GitHub Release` workflows. See `PUBLISHING.md` for the full procedure.

## Non-trivial work

For multi-step tasks (refactors, architectural investigations, work likely to span sessions), we use a task-folder workflow — see the **task-workflow
** skill (`.agents/skills/task-workflow/`). Invoke it via `/task-workflow` (if supported by your agent), or follow the protocol in the skill doc.
Completed tasks produce an ADR under `adr/`.

For trivial fixes, typos, and single-file edits, just do the work — no scaffolding needed.
