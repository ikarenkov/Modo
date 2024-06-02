# Modo

[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo-compose)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<list columns="2">
    <li>
        <p>
            <b>Modo</b> is a simple and convenient state-base navigation library for Jetpack Compose. It offers a flexible and powerful way to manage navigation in your application.
        </p>
    </li>
    <li>
        <p>
            <video src="../videos/modo_0.9.0_sample_overview.mp4" preview-src="../images/media/modo_0.9.0_sample_overview_preview.png" width="300"/>
        </p>
    </li>
</list>

## Overview of Modo

UI of Modo navigation is defined by `NavigationState`. `NavigationState` - is a structure of `Screen`
and `ContainerScreen`: ![diagram_1.png](diagram_1.png){
height = 400 }

### Screen

`Screen` is a basic unit of UI, leaf of the navigation graph, cannot contains other `Screen`s inside. To render content, create `Screen`
implementation and override `fun Content(modifier: Modifier)`

```kotlin
```

{ src="SampleScreen.kt" default-state="collapsed" collapsible="true" collapsed-title="SampleScreen"}

### Performing navigation

To update state, use `dispatch(action: Action)` on `NavigationContainer`, or build-in extension functions
for [StackScreen]()
and [MultiScreen]()

* `Screen` can be injected into your DI container, but it must be removed from DI as soon as `Screen` is removed from hierarchy. You can use build-in
  screen effects for this purpose.

## Getting Started

To get started with Modo, check out our [Quick Start Guide](QuickStartGuide.md) and explore
the [sample application](https://github.com/ikarenkov/Modo/tree/dev/sample) to see how Modo can simplify your navigation needs.

## List of features

<table>
<tr><td>Feature</td><td>Status</td><td>Source/Docs</td><td>Sample</td></tr>
<tr><td>Stack navigation</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/stack/StackScreen.kt">StackScreen</a>, <a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/stack/StackActions.kt">build-in commands</a></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/SampleStack.kt">SampleStack</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/stack/StackActionsScreen.kt">sample</a> of navigation actions</td></tr>
<tr><td>Multi screen navigation</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/multiscreen/MultiScreen.kt">MultiScreen</a>, <a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/multiscreen/MultiScreenActions.kt">build-in commands</a></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/SampleMultiScreen.kt">SampleMultiscreen</a></td></tr>
<tr><td>Dialogs</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/DialogScreen.kt">DialogScreen</a></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/dialogs/DialogsPlayground.kt">Dialogs playground</a></td></tr>
<tr><td>Arguments</td><td>✅</td><td>Arguments can be passed as constructor parameter of Screen.</td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/MainScreen.kt">MainScreen</a></td></tr>
<tr><td>Animation</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/animation/ScreenTransitions.kt">ScreenTransition</a>, <a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/animation/StackTransitionType.kt">helpers for stack screen transition</a></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/SampleStack.kt">SampleStack</a></td></tr>
<tr><td>Android lifecycle support</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/android/ModoScreenAndroidAdapter.kt">ModoScreenAndroidAdapter</a> connect Modo with android-related features. You can safely use LocalLifecycleOwner and other fun inside Screen's. To correctly observe screen lifecycle it's recommender to use <code>LifecycleScreenEffect</code></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/MainScreen.kt">MainScreen</a></td></tr>
<tr><td>Android ViewModel support</td><td>✅</td><td><a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/android/ModoScreenAndroidAdapter.kt">ModoScreenAndroidAdapter</a> connect Modo with android-related features. You can safely use <code>viewModel</code> and other fun inside Screen's.</td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/viewmodel/AndroidViewModelSampleScreen.kt">AndroidViewModelSampleScreen.kt</a></td></tr>
<tr><td>Activity and fragment integration</td><td>✅</td><td>Use <code>rememberRootScreen</code> inside <code>Fragment</code> or <code>Activity</code>. These functions are declared in <a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/Modo.kt">Modo</a> and automatically handle saving and restoring during <code>Activity</code> lifecycle and process death.</td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/ModoSampleActivity.kt">ModoSampleActivity</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/fragment/ModoFragment.kt">ModoFragment.kt</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/ModoLegacyIntegrationActivity.kt">ModoLegacyIntegrationActivity.kt</a></td></tr>
<tr><td>Activity and process death handling</td><td>✅</td><td>Automatically provided by <code>Fragment</code> and <code>Activity</code> Modo integration. For more details take a look to</td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/ModoSampleActivity.kt">ModoSampleActivity</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/fragment/ModoFragment.kt">ModoFragment.kt</a></td></tr>
<tr><td>Screen effects</td><td>✅</td><td>You can use <code>LaunchedScreenEffect</code> and <code>DisposableScreenEffect</code> analogies for <code>Screen</code>, it's linked to life time of a <code>Screen</code>. There is also <code>LifecycleScreenEffect</code> for easy observing lifecycle. See <a href="%github_code_url%modo-compose/src/main/java/com/github/terrakok/modo/lifecycle/ScreenEffects.kt">ScreenEffects</a> for details.</td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/ScreenEffectsSampleScreen.kt">ScreenEffectsSampleScreen.kt</a></td></tr>
<tr><td>Pager and LazyList integration</td><td>✅</td><td>You can create custom <code>ContainerScreen</code> and use internal <code>Screen</code> inside <code>Vertical/HorizontalPager</code> and <code>LazyRow/Column</code>. It's vital to define <code>key = {}</code> lambda with <code>InternalContent</code> for correct intagration. For more details take a look to the samples. <a href="%github_code_url%modo-compose%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fterrakok%2Fmodo%2Flazylist%2FLazyListUtils.kt">LazyListUtils</a></td><td><a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/StackInLazyColumnScreen.kt">StackInLazyColumnScreen.kt</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/HorizontalPagerScreen.kt">HorizontalPagerScreen.kt</a>, <a href="%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/containers/custom/RemovableItemContainerScreen.kt">RemovableItemContainerScreen.kt</a></td></tr>
</table>

