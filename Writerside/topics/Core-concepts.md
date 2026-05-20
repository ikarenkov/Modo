# Core Concepts

Modo is a state-based navigation library for Jetpack Compose. It represents the UI as a structure of `Screen`s and `ContainerScreen`s (which are
implementations of `Screen`).

<include from="snippets.topic" element-id="under_develop_note"/>

<include from="snippets.topic" element-id="navigation_is_a_graph"/>

## Screen

A `Screen` is the basic unit of the UI. It displays content defined in the overridden `fun Content(modifier: Modifier)`.

```kotlin
```

{ src="SampleScreen.kt" include-lines="1-20"}

### Screen Key

Each screen is identified by a `ScreenKey` - a unique key representing the screen. This key is extensively used in internal implementation. It must be
unique for each screen, even after process death. To ensure this, use the built-in `generateScreenKey` function.

### Arguments { id="arguments"}

To pass arguments to the screen, declare them in the Screen's constructor:

```kotlin
```

{ src="SampleScreen.kt" include-lines="2-5,7-8" }

### Saving and Restoring

Each screen is `Parcelable`, which helps to save and restore it during lifecycle changes. Use the <tooltip term="parcelize">
parcelable</tooltip> [Gradle plugin](https://developer.android.com/kotlin/parcelize) and the `@Parcelize` annotation to generate the `Parcelable`
implementation on the fly.

It's crucial to use built-in functions like `rememberRootScreen` to integrate Modo with your application.
Read [](How-to-integrate-modo-to-your-app.md) for details.

## ContainerScreen

{ id="container-screen" }

`ContainerScreen`s are types of screens that can contain other screens. They are fundamental building blocks for complex navigation
structures. [`StackScreen`](StackScreen.md) and `MultiScreen` are built-in implementations of `ContainerScreen`.

![diagram_2.png](diagram_2.png){ height = 300 }

Each ContainerScreen is parameterized by its `State` type.

```kotlin
@Stable
abstract class ContainerScreen<State : NavigationState>(
    private val navModel: NavModel<State>
) : Screen, NavigationContainer<State> by navModel 
```

{ collapsible="true" default-state="collapsed" collapsed-title="ContainerScreen"}

<procedure>
<title>State</title>
<p>
<code>NavigationState</code> - a class that can contain nested screens and other additional information. The state can be updated by calling <code>dispatch(reducer)</code>.
</p>
<code-block lang="kotlin" collapsible="true" default-state="collapsed" collapsed-title-line-number="2">
@Parcelize
data class SampleState(
  val screen1: Screen,
  val screen2: Screen,
  val screen3: Screen?,
) : NavigationState {
  // You need to return all nested screens to ensure correct functionality.
  override fun getChildScreens(): List<Screen> = listOfNotNull(screen1, screen2, screen3)
}
</code-block>

Read the <a href="#state-update">State Update</a> section for more details.

</procedure>

<procedure>
<title>Reducer</title>
<p>
<code>NavigationReducer</code> - a pure state transformer that takes the old state and returns the new one. Dispatch it via <code>dispatch(reducer)</code> to update the container's state:
<code-block src="SampleAction.kt" lang="kotlin" collapsible="true" default-state="collapsed" include-lines="1-12"/>

</p>

</procedure>

`NavModel` - a state storage responsible for state updates and triggering UI updates. Each ContainerScreen has a NavModel as a constructor parameter.

### Rendering Nested Screens

To render nested screens inside a container screen, you **must** use the `InternalContent` function. This function provides all necessary
integrations, such as:

* Correct usage of `rememberSaveable` inside nested screens by using `SaveableStateHolder`.
* Integration of `ScreenModel`, ensuring consistency for the same screen and clearing it when the `Screen` leaves the hierarchy.
* Android integration, including `Lifecycle` and `ViewModel` support.

The built-in `StackScreen` and `MultiScreen` use `InternalContent` under the hood to ensure correct nested screen functionality.

## State Update

The simplest way to update a `ContainerScreen`'s state is to dispatch a lambda that calculates the new state from the old one. For example, to push two new screens onto a `StackScreen`:

```kotlin
stackContainer.dispatch { oldState ->
    StackState(oldState.stack + listOf(NextScreen(), AnotherScreen()))
}
```

The built-in containers already expose convenience extension functions for the most common operations, so the same change can be written as:

```kotlin
stackContainer.forward(NextScreen(), AnotherScreen())
```

Explore the available commands in [`StackActions.kt`](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/stack/StackActions.kt) (`forward`, `back`, `replace`, …) and [`MultiScreenActions.kt`](%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/multiscreen/MultiScreenActions.kt).

If you need reusable or parameterized state changes, define your own `NavigationReducer<State>`. Pick whichever shape fits your code:

- named instances on an object (the `SampleReducer` / `SampleReducers` example above)
- a class with constructor parameters — see [`RemoveTabReducer`](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/RemoveTabReducer.kt) in the sample app
- your own extension functions on a typed `NavigationContainer<YourState>` for ergonomic call sites

## Root Screen

To integrate Modo into your application, use one of the built-in functions from the Modo file. It returns a `RootScreen`, which provides
a `SaveableStateHolder`.