# Lifecycle

This article covers the lifetime of screen instances and their integration with the Android Lifecycle.

## Screen Instance Lifecycle

The lifetime of a screen instance is guaranteed to match the application (process) lifetime when you integrate Modo using built-in functions such as
`Modo.rememberRootScreen`. Regardless of how many times a screen is recomposed, or whether the activity and/or fragment is recreated, the screen
instance remains consistent. This allows you to safely inject the screen instance into your DI container.

## Android Lifecycle Integration

Modo provides seamless [integration](%github_code_url%/modo-compose/src/main/java/com/github/terrakok/modo/android/ModoScreenAndroidAdapter.kt)
with a Android Lifecycle for your screens.

You can use `LocalLifecycleOwner` inside `Screen.Content` to access the lifecycle of a screen. This will return the nearest screen's lifecycle owner.

```kotlin
class SampleScreen : Screen {
    override fun Content(modifier: Modifier) {
        val lifecycleOwner = LocalLifecycleOwner.current
        // Use lifecycleOwner to observe lifecycle events
    }
}
```

### Lifecycle States and Events

Here’s an overview of lifecycle states and their meanings in the context of the screen lifecycle:

| **State**       | **Meaning**                                                                                                                 |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------|
| **INITIALIZED** | The screen is constructed (instance created) but has never been displayed.                                                  |
| **CREATED**     | The screen is in the navigation hierarchy and can be reached from the `RootScreen` integrated with an Activity or Fragment. |
| **STARTED**     | `Screen.Content` is in composition.                                                                                         |
| **RESUMED**     | The screen is **STARTED**, and there are no unfinished transitions for this screen or its parent.                           |
| **DESTROYED**   | The screen is removed from the navigation graph.                                                                            |

> `ON_CREATE` and `ON_DESTROY` are dispatched once per screen instance.

### Screen Transitions and Lifecycle

Modo provides a convenient way to track when screen transitions start and finish. These events are tied to the `ON_RESUME` and `ON_PAUSE` lifecycle
events. Here’s a summary:

| **Event**     | **With Transition**                                                                            | **Without Transition**                      |
|---------------|------------------------------------------------------------------------------------------------|---------------------------------------------|
| **ON_RESUME** | Dispatched when there are no unfinished transitions, and the parent is in the `RESUMED` state. | Dispatched when the parent is in `RESUMED`. |
| **ON_PAUSE**  | Dispatched when a hiding transition starts.                                                    | Dispatched immediately before `ON_STOP`.    |

### Parent-Child Lifecycle Propagation

The lifecycle of parent and child screens follows a set of rules, ensuring consistency and predictability:

1. A screen's `Lifecycle.State` is always less than or equal to (`<=`) its parent's state.
2. A child screen is not moved to the `RESUMED` state until its parent is also in the `RESUMED` state.
3. When a screen's lifecycle state is downgraded, its child screens are also moved to the same state.
4. When a screen reaches the `RESUMED` state and its child screens are ready to resume, the children's lifecycles are also moved to `RESUMED`.

### Practical Example: Keyboard Management

A practical use case for these lifecycle events is managing the keyboard. For example, you can show and hide the keyboard using `ON_RESUME` and
`ON_PAUSE` events:

* `ON_RESUME` indicates that the screen is ready for user input (transitions are finished)
* `ON_PAUSE` indicates that the screen is not ready for user input (transitions are starting)

```kotlin
val lifecycleOwner = LocalLifecycleOwner.current
val keyboardController = LocalSoftwareKeyboardController.current
DisposableEffect(this) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                focusRequester.requestFocus()
            }
            Lifecycle.Event.ON_PAUSE -> {
                focusRequester.freeFocus()
                keyboardController?.hide()
            }
            else -> {}
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}
TextField(text, setText, modifier = Modifier.focusRequester(focusRequester))
```