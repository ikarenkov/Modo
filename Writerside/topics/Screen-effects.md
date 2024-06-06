# Screen effects

Modo provides a [set of side-effect's](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/lifecycle) that are similar to
the [`LaunchedEffect`](https://developer.android.com/develop/ui/compose/side-effects#launchedeffect)
and [`DisposableEffect`](https://developer.android.com/develop/ui/compose/side-effects#disposableeffect) from Jetpack Compose, but
they are tied to the screen lifecycle:

* `LaunchedScreenEffct` - executes the given `block: suspend CoroutineScope.() -> Unit` when the screen is created and cancels coroutine scope when
  the screen is removed.
* `DisposableScreenEffect` - executes the given `effect: DisposableEffectScope.() -> DisposableEffectResult` whe the screen is created and calls
  the `DisposableEffectResult.dispose()` of returned `DisposableEffectResult` when the screen is removed.
* `OnScreenRemoved` - executes when the screen is removed from the navigation graph. It is similar to the `DisposableScreenEffect`.
* `LifecycleScreenEffect` - a shortcut for subscribing to screen's lifecycle, that uses `DisposableScreenEffect` under the hood.

You can use them when you need to perform some action when screen is created and then dispose it when screen is removed. F.e. you can use it to create
and close DI scope.

1. effects called called once per `Screen` instance, when the given fun enters to the composition and this block wasn't called for
   this `Screen`'s instance.
2. Effect disposed when the `Screen` is removed from the navigation graph.

## Examples of usage

### Analytics

You can use `DisposableScreenEffect` to track screen first visit and remove events:

```Kotlin
DisposableScreenEffect {
    logcat { "Analytics: screen created. Counter: $counter." }
    onDispose {
        logcat { "Analytics: screen destroyed.  Counter: $counter." }
    }
}
```

### DI scope integration

You can use `OnScreenRemoved` with `remember` to create and close DI scope. Scope of `Screen` can be identified by `screenKey`. Here is a sample of
how you can use it with <include from="snippets.topic" element-id="toothpick"/>:

```Kotlin
```

{ src="ToothpickIntegrationSample.kt" }
