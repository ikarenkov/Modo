# Lifecycle

This article covers the lifetime of screen instances and their integration with Android Lifecycle.

## Screen instance lifecycle

It is guaranteed that the screen instance lifetime is equal to application (process) lifetime, when you provide Modo integration using built-in
function from `Modo`, such as`Modo.rememberRootScreen`. No matter how many times a screen is recomposed, activity or/and fragment is recreated etc.

## Android Lifecycle

Modo provides Android Lifecycle integration for your screens.
You can use `LocalLifecycleOwner` inside  `Screen.Content` to access the lifecycle of a screen.
It will return the nearest Screen's lifecycle owner.

```kotlin
class SampleScreen : Screen {
    override fun Content(modifier: Modifier) {
        val lifecycleOwner = LocalLifecycleOwner.current
        // Use lifecycleOwner to observe lifecycle events
    }
}
```

### Lifecycle states and events

Let's take a look at what specific lifecycle states mean in the context of Screen Lifecycle:

<table>
    <tr>
        <th>State</th>
        <th>Meaning</th>
    </tr>
    <tr>
        <td><b>INITIALIZED</b></td>
        <td>Screen is constructed (instance created), but it has never been displayed.</td>
    </tr>
    <tr>
        <td><b>CREATED</b></td>
        <td>

Screen is in navigation hierarchy, it can be reached from RootScreen, that is integrated to Activity/Fragment. With other words, it was displayed at
least once.

</td>
    </tr>
    <tr>
        <td><b>STARTED</b></td>
        <td>

`Screen.Content` is in composition.

</td>
    </tr>
    <tr>
        <td><b>RESUMED</b></td>
        <td>

**STARTED** and there is no unfinished transitions for this screen or it's parent.
</td>
    </tr>
    <tr>
        <td><b>DESTROYED</b></td>
        <td>Screen is removed from the navigation graph.</td>
    </tr>
</table>

To clarify, let's take a look at the lifecycle events:

* `ON_CREATE` and `ON_DESTROY` are dispatched once per screen instance.

### Screen transitions and lifecycle

Modo provides convenient way to determine whenever screen's appearing/disappearing transitions are started or finished. To observe these events, you
can rely on `ON_RESUME` and `ON_PAUSE` lifecycle events, check out the table

<table>
    <tr>
        <th>Event</th>
        <th>With transition</th>
        <th>Without transition</th>
    </tr>
    <tr>
        <td><b>ON_RESUME</b></td>
        <td>

Dispatched when as soon as there are no unfinished transitions for this screen and parent is in `State.RESUMED`.
</td>
        <td>

Parent is `RESUMED`
</td>
    </tr>
    <tr>
        <td><b>ON_PAUSE</b></td>
        <td>
Dispatched when hiding transition is started.
</td>
        <td>

Dispatched right before `ON_STOP`.
</td>
    </tr>
</table>

### Parent-Child Lifecycle propagation

There is a set of rules between lifecycle of parent and child screens, which allows you to relay on your screen's lifecycle and don't worry about
parent's lifecycle:

1. Screen's `Lifecycle.State` is always lower or equal (<=) than the state of its parent.
2. If child's lifecycle is ready to be in `RESUMED` state, it is not resumed until parent's lifecycle is `RESUMED` too.
3. When screen`s lifecycle is moved down, it also moves children lifecycle to the same state.
4. When screen's lifecycle is `RESUMED` and children's lifecycle is ready to be resumed, children's lifecycle is resumed too.

Practical example of this is using events `ON_RESUME` and `ON_PAUSE` to show and hide keyboard:

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