Here's the improved version of your documentation text:

# Modo Overview

[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo-compose)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<list columns="2">
<li>
    <video src="../videos/modo_0.9.0_sample_overview.mp4" preview-src="../images/media/modo_0.9.0_sample_overview_preview.png" width="300" mini-player="true" />
</li>
<li>
    <b>Modo</b> is a simple and convenient state-based navigation library for Jetpack Compose. It offers a flexible and powerful way to manage navigation in your application.
    <p>
        The UI of Modo navigation is defined by <code>NavigationState</code>, which is a structure of <code>Screen</code>
        and <code>ContainerScreen</code>. You can easily modify <code>NavigationState</code> to suit your needs from any part of your application.
    </p>
</li>
</list>

## Main Idea

<include from="snippets.topic" element-id="navigation_is_a_graph"/>

## Convenient Navigation

Modo is an easy-to-use library. Here are some of the most-used features of Modo navigation:

* `Screen` is the basic unit of the UI. It displays content defined in the overridden `fun Content(modifier: Modifier)`.

  ```kotlin
  ```
  { src="OverviewSampleScreen.kt" default-state="collapsed" }

* To navigate between screens in a stack, simply call `forward()` and `back()` on `StackScreen`, which is an implementation of `ContainerScreen`.

  ```kotlin
  // 1. Taking the nearest stack screen
  val stackNavigation = LocalStackNavigation.current
  // 2. Performing navigation
  val onForwardClick = { stackNavigation.forward(SampleScreen()) }
  ```

* You can easily change `NavigationState` as needed by calling `dispatch(action: (StackState) -> StackState)` on `NavigationContainer`:

  ```kotlin
  navigation.dispatch { oldState ->
      StackState(
          oldState.stack.filterIndexed { index, screen ->
              index % 2 == 0 && screen != oldState.stack.last()
          }
      )
  }
  ```

* To integrate Modo with your application, use `rememberRootScreen` inside a `Fragment` or `Activity`. You can use `DefaultStackScreen` as a
  default stack implementation.

  ```kotlin
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

## [List of Features](Features.topic)