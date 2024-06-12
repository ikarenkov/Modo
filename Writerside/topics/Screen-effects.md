Here’s the improved version of your documentation text:

# Screen Effects

Modo provides a [set of side effects](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/lifecycle) that are similar to
the [`LaunchedEffect`](https://developer.android.com/develop/ui/compose/side-effects#launchedeffect)
and [`DisposableEffect`](https://developer.android.com/develop/ui/compose/side-effects#disposableeffect) from Jetpack Compose, but they are tied to
the screen lifecycle:

* `LaunchedScreenEffect` - Executes the given `block: suspend CoroutineScope.() -> Unit` when the screen is created and cancels the coroutine scope
  when the screen is removed.
* `DisposableScreenEffect` - Executes the given `effect: DisposableEffectScope.() -> DisposableEffectResult` when the screen is created and calls
  the `DisposableEffectResult.dispose()` of the returned `DisposableEffectResult` when the screen is removed.
* `OnScreenRemoved` - Executes when the screen is removed from the navigation graph. It is similar to the `DisposableScreenEffect`.
* `LifecycleScreenEffect` - A shortcut for subscribing to a screen's lifecycle, using `DisposableScreenEffect` under the hood.

You can use these effects when you need to perform an action when the screen is created and then dispose of it when the screen is removed. For
example, you can use it to create and close a DI scope.

1. Effects are called once per `Screen` instance, when the given function enters the composition and this block hasn't been called for this `Screen`
   instance.
2. The effect is disposed of when the `Screen` is removed from the navigation graph.

## Examples of Usage

### Analytics

You can use `DisposableScreenEffect` to track screen creation and removal events:

```kotlin
DisposableScreenEffect {
    logcat { "Analytics: screen created. Counter: $counter." }
    onDispose {
        logcat { "Analytics: screen destroyed. Counter: $counter." }
    }
}
```

### DI Scope Integration

You can use `OnScreenRemoved` with `remember` to create and close a DI scope. The scope of a `Screen` can be identified by `screenKey`. Here is a
sample of how you can use it with <include from="snippets.topic" element-id="toothpick"/>:

```kotlin
```

{ src="ToothpickIntegrationSample.kt" }