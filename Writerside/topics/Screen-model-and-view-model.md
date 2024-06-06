# Screen model and view model

Modo provides support of [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and also provides it's
own `ScreenModel`.

## ScreenModel

ScreenModel is similar to ViewModel, it is a interface that has `onDispose` function which is called when `ScreenModel` needs to be cleared.

To use it inside screen, call `rememberScreenModel` inside `Content` function:

```Kotlin
```

{ src="ScreenModelSample.kt" }

## ViewModel

You can use `ViewModel` from Android Jetpack in Modo:



<include from="snippets.topic" element-id="under_develop_note"></include>