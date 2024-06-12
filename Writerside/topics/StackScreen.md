# StackScreen - Stack Navigation

`StackScreen` is a basic [ContainerScreen](Core-concepts.md#container-screen) that represents a stack of screens and renders the last one by default.
It is a simple and convenient way to implement stack navigation in your application.

![stack_screen.png](stack_screen.png)

To use a stack screen in your application, you can simply use `DefaultStackScreen` for the default implementation of the stack with a default
fade-in/out animation for transitions:

```kotlin
val stackScreen = DefaultStackScreen(
    // Navigation model that controls the state
    StackNavModel(
        // Declaring initial screens in the stack, this stack has 1 screen
        SampleScreen(screenIndex = 1)
    )
)
```

You can change the stack by calling `dispatch(Action)` on `NavigationContainer<StackState, StackAction>`.

For a convenient way to update the state, there is a function `dispatch(action: (StackState) -> StackState)` that allows you to change the state
according to your needs. There is also a list of built-in commands.

## Built-in Navigation Actions

Modo provides a list of built-in actions for stack navigation. You can explore the available
commands [here](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/stack/StackActions.kt). Some of them include:

* `forward(screen: Screen, vararg screens: Screen)` - Adds the given screens to the top of the stack.
* `replace(screen: Screen, vararg screens: Screen)` - Replaces the top screen of the stack with the provided screens.
* `back`/`backTo` - A variety of commands for popping screens from the backstack.
* `backToRoot` - Drops all screens except the first one.
* `removeScreens(condition: (pos: Int, screen: Screen) -> Boolean)` - Removes screens that satisfy the condition from the stack.

## Accessing the Nearest StackScreen

To access the nearest StackScreen from a composable function, you can use the special composition local `LocalStackNavigation`. It can be used for
further actions.

## Animation

<include from="snippets.topic" element-id="under_develop_note"/>