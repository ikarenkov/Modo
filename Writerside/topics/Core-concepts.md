# Core concepts

Modo is a state-based navigation library for jetpack compose. It represents UI as a structure of `Screen`s and `ContainerScreen`s (which is an
implementation of `Screen`).

<include from="snippets.topic" element-id="navigation_is_a_graph"/>

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