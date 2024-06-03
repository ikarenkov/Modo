# Modo overview

[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo-compose)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<list columns="2">
<li>
    <video src="../videos/modo_0.9.0_sample_overview.mp4" preview-src="../images/media/modo_0.9.0_sample_overview_preview.png" width="300" mini-player="true" />
</li>
<li>
    <b>Modo</b> is a simple and convenient state-base navigation library for Jetpack Compose. It offers a flexible and powerful way to manage navigation in your application.
    <p>
        UI of Modo navigation is defined by `NavigationState`. `NavigationState` - is a structure of `Screen`
        and `ContainerScreen`. You can easily change `NavigationState` depends on your needs from the any place of your application.
    </p>
</li>
</list>

## The main idea

<include from="snippets.topic" element-id="navigation_is_a_graph"/>

## Convenient navigation

Modo is easy to use library, there are some most-used features of Modo navigation:

* `Screen` is a basic unit of UI. It displays content defined in overridden `fun Content(modifier: Modifier)`

  ```kotlin
  ```
  { src="OverviewSampleScreen.kt" default-state="collapsed" }
* To navigate between screens on stack, you can simply call `forward()` and `back()` on `StackScreen` witch is implementation of `ContainerScreen`.

  ```Kotlin
  // 1. Taking a nearest stack screen
  val stackNavigation = LocalStackNavigation.current
  // 2. Performing navigation
  val onForwardClick = { stackNavigation.forward(SampleScreen()) }
  ```

* You can easily change `NavigationState` as you wish by calling `dispatch(action: (StackState) -> StackState)` on `NavigationContainer`:

  ```Kotlin
  navigation.dispatch { oldState ->
      StackState(
          oldState.stack.filterIndexed { index, screen ->
              index % 2 == 0 && screen != oldState.stack.last()
          }
      )
  }
  ```

* To integrate Modo with your application, you can use `rememberRootScreen` inside `Fragment` or `Activity`. You can use `DefaultStackScreen` as a
  default implementation of stack.

  ```Kotlin
  setContent {
      val rootScreen = rememberRootScreen { 
        DefaultStackScreen(SampleScreen()) 
      }
      rootScreen.Content(Modifier.fillMaxSize())
  }
  ```
  {prompt="Activity: "}

## Getting Started

To get started with Modo, check out our [Quick Start Guide](QuickStartGuide.md) and explore
the [sample application](https://github.com/ikarenkov/Modo/tree/dev/sample) to see how Modo can simplify your navigation needs.

## [List of features](Features.md)

