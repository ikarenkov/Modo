# How to integrate modo into your app

<link-summary>This topic provides guide for available app integrations. It also describes explains some details of the implementation.</link-summary>

To provide correctness of navigation, Modo requires usage of build-in functions for integration.
To integrate Modo to your app you have 2 approaches:

* `rememberRootScreen` - convenient integration to `Activity` or `Fragment`. You call it inside `setContent` function.
* `Modo.getOrCreateRootScreen`, `Modo.save` and `Modo.onRootScreenFinished` - for manual integration.

> `rememberRootScreen` and `Modo.getOrCreateRootScreen` return the same instance of `RootScreen` in the same process, so you can safely inject it
> into your DI
> container.

{ style="note" }

## Convenient integration to Activity and Fragment

`rememberRootScreen` is a convenient way to integrate Modo to your `Activity` or `Fragment`. It automatically handles saving and restoring
of `RootScreen` during `Activity` lifecycle and process death.

To use modo inside your Activity or Fragment you need:

1. Use `rememberRootScreen` inside `setContent` function and pass Screen that you want to render. You will have instance of the `RootScreen` for
   further displaying.
2. Display content by calling `fun Content(modifier: Modifier)` on created instance of `RootScreen`

<tabs>
    <tab title="Activity">
        <code-block src="QuickStartActivity.kt" lang="kotlin"/>
    </tab>
    <tab title="Fragment">
        <code-block src="ModoFragment.kt" lang="kotlin"/>
        <note>
            For Fragments we create <code>ComposeView</code> and call <code>setContent</code> on it. So don't forget to use
            <code-block lang="kotlin">
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
            </code-block>
            for your <code>ComposeView</code> for Fragment. <a href="https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views#compose-in-fragments">Documentation reference.</a>
        </note>
    </tab>
</tabs>

## Manual integration

If you want to have more control over Modo integration, you can use `Modo.getOrCreateRootScreen`, `Modo.save` and `Modo.onRootScreenFinished`
functions. Check-out
the [ModoLegacyIntegrationActivity](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/ModoLegacyIntegrationActivity.kt) in the
sample project for an example.

- `Modo.getOrCreateRootScreen` - initializes `RootScreen` with provided screen's or returns the existing instance.
- `Modo.save` - saves the current state of `RootScreen` and other internal data (like `screenCounterKey`) to the bundle for further restore
  in `Modo.getOrCreateRootScreen`.
- `Modo.onRootScreenFinished` - should be called when `RootScreen` is no longer needed, f.e. at the finish of the Activity or the Fragment. It removes
  the instance of `RootScreen` from the
  internal runtime-cache and clears the other internal resources.

<tabs>
    <tab title="Activity">
        <code-block src="ModoManualIntegrationActivity.kt" lang="kotlin"/>
    </tab>
    <tab title="Fragment">
        TBD
    </tab>
</tabs>