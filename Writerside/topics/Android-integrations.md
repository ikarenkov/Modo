# Android Integrations

Modo provides Android integrations with ViewModel and Lifecycle support through `LocalLifecycleOwner`, `LocalViewModelStoreOwner`,
and `LocalSavedStateRegistryOwner`. This allows you to use functions like `viewModel` to obtain Android `ViewModel`.

> [Sample code](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/viewmodel/AndroidViewModelSampleScreen.kt) demonstrating
> Android integration inside a Modo Screen.

## ViewModel

You can use functions provided by Jetpack Compose to get a `ViewModel`.

<include from="snippets.topic" element-id="under_develop_note"/>

## Lifecycle

Moved to [Lifecycle](Lifecycle.md).