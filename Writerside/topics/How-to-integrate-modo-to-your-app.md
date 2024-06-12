# How to Integrate Modo into Your App

<link-summary>This guide provides instructions for integrating Modo into your app, along with details of the implementation.</link-summary>

To ensure the correctness of navigation, Modo requires the use of built-in functions for integration. You have two approaches to integrate Modo into
your app:

* `rememberRootScreen` - a convenient integration for `Activity` or `Fragment`. Use it inside the `setContent` function.
* `Modo.getOrCreateRootScreen`, `Modo.save`, and `Modo.onRootScreenFinished` - for manual integration.

> `rememberRootScreen` and `Modo.getOrCreateRootScreen` return the same instance of `RootScreen` within the same process, so you can safely inject it
> into your DI container.

{ style="note" }

## Convenient Integration for Activity and Fragment

`rememberRootScreen` is an easy way to integrate Modo into your `Activity` or `Fragment`. It automatically handles the saving and restoring
of `RootScreen` during the `Activity` lifecycle and process death.

To use Modo inside your Activity or Fragment, follow these steps:

1. Use `rememberRootScreen` inside the `setContent` function and pass the screen you want to render. This will give you an instance of
   the `RootScreen` for further display.
2. Display content by calling `fun Content(modifier: Modifier)` on the created instance of `RootScreen`.

<tabs>
    <tab title="Activity">
        <code-block src="QuickStartActivity.kt" lang="kotlin"/>
    </tab>
    <tab title="Fragment">
        <code-block src="ModoFragment.kt" lang="kotlin"/>
        <note>
            For Fragments, create a <code>ComposeView</code> and call <code>setContent</code> on it. Remember to use
            <code-block lang="kotlin">
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
            </code-block>
            for your <code>ComposeView</code> in the Fragment. <a href="https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views#compose-in-fragments">Documentation reference.</a>
        </note>
    </tab>
</tabs>

## Manual Integration

If you prefer more control over Modo integration, you can use `Modo.getOrCreateRootScreen`, `Modo.save`, and `Modo.onRootScreenFinished`. Check out
the [ModoLegacyIntegrationActivity](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/ModoLegacyIntegrationActivity.kt) in the
sample project for an example.

- `Modo.getOrCreateRootScreen` - initializes `RootScreen` with the provided screen or returns the existing instance.
- `Modo.save` - saves the current state of `RootScreen` and other internal data (like `screenCounterKey`) to the bundle for future restoration
  in `Modo.getOrCreateRootScreen`.
- `Modo.onRootScreenFinished` - should be called when `RootScreen` is no longer needed, such as when the Activity or Fragment finishes. It removes the
  instance of `RootScreen` from the internal runtime cache and clears other internal resources.

<tabs>
    <tab title="Activity">
        <code-block src="ModoManualIntegrationActivity.kt" lang="kotlin"/>
    </tab>
    <tab title="Fragment">
        TBD
    </tab>
</tabs>
