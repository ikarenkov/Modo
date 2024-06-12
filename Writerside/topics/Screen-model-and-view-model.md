# Screen Model and ViewModel

Modo provides support for [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and also includes its
own `ScreenModel`.

## ScreenModel

`ScreenModel` is similar to `ViewModel`. It is an interface that has an `onDispose` function, which is called when the `ScreenModel` needs to be
cleared.

To use it inside a screen, call `rememberScreenModel` inside the `Content` function:

```kotlin
```

{ src="ScreenModelSample.kt" }

## ViewModel

You can use `ViewModel` from Android Jetpack in Modo:

<include from="snippets.topic" element-id="under_develop_note"/>