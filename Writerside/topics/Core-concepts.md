# Core concepts

Modo is a state-based navigation library for jetpack compose. It represents UI as a structure of `Screen`s and `ContainerScreen`s (which is an
implementation of `Screen`).

<include from="snippets.topic" element-id="under_develop_note"/>

<include from="snippets.topic" element-id="navigation_is_a_graph"/>

## Screen

`Screen` is a basic unit of UI. It displays content defined in overridden `fun Content(modifier: Modifier)`

```kotlin
```

{ src="SampleScreen.kt" include-lines="1-20"}

### Screen key

Each screen is identified by `ScreenKey` - a unique key that represents the screen.
This key is widely used in internal implementation. It is required to be unique per a screen, even after process death. For this you must use
build-in `generateScreenKey` function.

### Arguments { id="arguments"}

To pass arguments to the screen, declare it in the Screen's constructor:
    
```Kotlin
```

{ src="SampleScreen.kt" include-lines="2-5,7-8" }

### Saving and restoring

Each screen is `Parcelable`, that helps to save and restore it during lifecycle changes. Use <tooltip term="parcelize">
parcelable</tooltip> [gradle plugin](https://developer.android.com/kotlin/parcelize) and `@Parcelable` annotation to generate `Parcelable`
implementation on the fly.

It's vital to use build-in function like `rememberRootScreen` to integrate Modo with your application. Read [](How-to-integrate-modo-to-your-app.md)
for details.

## ContainerScreen { id="container-screen" }

`ContainerScreen`s are the type of screens that can contain other screens. It's a basic building block for complex navigation structures.
[`StackScreen`](StackScreen.md) and `MultiScreen` are build-in implementations of `ContainerScreen`.

![diagram_2.png](diagram_2.png){ height = 300 }

Each ContainerScreen is defined by 2 typed parameters: State and Action.

```Kotlin
@Stable
abstract class ContainerScreen<State : NavigationState, Action : NavigationAction<State>>(
    private val navModel: NavModel<State, Action>
) : Screen, NavigationContainer<State, Action> by navModel 
```

{ collapsible="true" default-state="collapsed" collapsed-title="ContainerScreen"}

<procedure>
<title>State</title>
<p>
<code>NavigationState</code> - class that can contains nested screens and other additional info. State can be updated by calling <code>dispatch(action)</code>.
</p>
<code-block lang="kotlin" collapsible="true" default-state="collapsed" collapsed-title-line-number="2">
@Parcelize
data class SampleState(
  val screen1: Screen,
  val screen2: Screen,
  val screen3: Screen?,
) : NavigationState {
  // You need to return all nested screens in order to provide correct work.
  override fun getChildScreens(): List<Screen> = listOfNotNull(screen1, screen2, screen3)
}
</code-block>
</procedure>

<procedure>
<title>Action</title>
<p>
<code>NavigationAction</code> - marker interface to distinguish actions for this container on specific <code>State</code>. You can also use 
<code>ReducerAction</code> for defining actions with update function in-place:
<code-block src="SampleAction.kt" lang="kotlin" collapsible="true" default-state="collapsed"/>

</p>

</procedure>

<procedure>
<title>Updating State</title>
<p>
To update state of <code>ContainerScreen</code> you must use <code>dispatch(action: Action)</code>.
There are 2 ways how to define you action:
</p>
<step>
(Recommended) ReducerAction - allows to define update function in-place.
<code-block src="SampleAction.kt" lang="kotlin" collapsible="true" default-state="collapsed"/>
</step>
<step>
Custom reducer + Action. You can provide a reducer in you <code>ContainerScreen</code> implementation.
<code-block lang="kotlin" collapsible="true" default-state="collapsed">TODO</code-block>
</step>

</procedure>


`NavModel` - storage of state, that responsible for state updates and triggering updating UI. Each ContainerScreen has a NavModel as a constructor
parameter.

To render nested screens inside a container screen, you **must** use `InternalContent` function. This function provides all necessary integrations,
like:

* Correct work of `rememberSaveable` inside nested screens by using `SaveableStateHolder`
* `ScreenModel`'s integration, that should be the same for the same screen and be cleared when `Screen` leaves the hierarchy
* Android integration, like `Lifecycle` and `ViewModel` support

Build-in `StackScreen` and `MultiScreen` uses `InternalContent` under the hood, to provide correct work of nested screens.

## Root Screen

To integrate Modo into your application, you use one of the build-in functions from Modo file. It returns a `RootScreen`, that simply provides
a `SaveableStateHolder`.