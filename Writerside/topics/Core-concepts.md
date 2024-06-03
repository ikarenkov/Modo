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

### Arguments

To pass arguments - use parcelable constructor parameters.

### Saving and restoring

Each screen is `Parcelable`, that helps to save and restore it during lifecycle changes. Use <tooltip term="parcelize">
parcelable</tooltip> [gradle plugin](https://developer.android.com/kotlin/parcelize) and `@Parcelable` annotation to generate `Parcelable`
implementation on the fly.

It's vital to use build-in function like `rememberRootScreen` to integrate Modo with your application. Read [](How-to-integrate-modo-to-your-app.md)
for details.

## Container Screen

Container Screens are the type of screens that can contain other screens. The most used container screen is `StackScreen` that represents a stack of
screens and renders the last one.

![diagram_2.png](diagram_2.png){ height = 300 }

To render nested screens inside a container screen, you **must** use `InternalContent` function. This function provides all necessary integrations,
like:

* Correct work of `rememberSaveable` inside nested screens by using `SaveableStateHolder`
* `ScreenModel`'s integration, that should be the same for the same screen and be cleared when `Screen` leaves the hierarchy
* Android integration, like `Lifecycle` and `ViewModel` support

Build-in `StackScreen` and `MultiScreen` uses `InternalContent` under the hood, to provide correct work of nested screens.

## Root Screen

To integrate Modo into your application, you use one of the build-in functions from Modo file. It returns a `RootScreen`, that simply provides
a `SaveableStateHolder`.