# StackScreen - stack navigation

StackScreen is a basic [ContainerScreen](Core-concepts.md#container-screen) that represents a stack of screens and renders the last one by default. It is a simple and convenient way to
implement stack navigation in your application.

![stack_screen.png](stack_screen.png)

To use stack screen in your application you can simply use `DefaultStackScreen` for the default implementation of stack with a default fade in/out
animation for transitions:

```Kotlin
val stackScreen = DefaultStackScreen(
    // navigation model that controls state
    StackNavModel(
        // declaring initial screens in stack, this stack of 1 screen
        SampleScreen(screenIndex = 1)
    )
)
```

You can change the stack by calling `dispatch(Action)` on `NavigationContainer<StackState, StackAction>`.

For convenient way to update state there is a fun `dispatch(action: (StackState) -> StackState)` that allows to change the state depends on your
needs. Also there is a list of build-in commands.

## Build-in navigation actions

Modo provides list of build-in actions for stack navigation. You can explore available
commands [here](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/stack/StackActions.kt), there are some of them

* `forward(screen: Screen, vararg screens: Screen)` - adds given screens to the top of the stack.
* `replace(screen: Screen, vararg screens: Screen)` - replaces the top screen of the stack to the provided screens.
* `back`/`backTo` - variety of commands for popping screens from the backstack.
* `backToRoot` - drop all screens but not the first one.
* `removeScreens(condition: (pos: Int, screen: Screen) -> Boolean)` - remove screens that succeeds condition from the stack.

## Accessing nearest StackScreen

To access the nearest StackScreen from composable function you can use special composition local - `LocalStackNavigation`. It can be used for further
actions.

## Animation

<include from="snippets.topic" element-id="under_develop_note"/>