# Modo

[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo-compose)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Modo is a simple and convenient state-base navigation library for Jetpack Compose.

<table>
    <tr>
        <td>
            <img src="media/modo_base_navigation.gif" width="256"/>
        </td>
        <td>
            <img src="media/modo_multiscreen.gif" width="256"/>
        </td>
        <td>
            <img src="media/modo_nested_navigation.gif" width="256"/>
        </td>
    </tr>
    <tr>
        <td>
            Basic navigation
        </td>
        <td>
            MultiScreen navigation
        </td>
        <td>
            Nested navigation
        </td>
    </tr>
</table>

## The Main idea

* `NavigationState` defines UI
    * Initial state is defined in constructor of `ContainerScreen` by `navModel: NavModel<State, Action>`
    * To update state, use `dispatch(action: Action)` on `NavigationContainer`, or build-in extension functions
      for [StackScreen](modo-compose/src/main/java/com/github/terrakok/modo/stack/StackActions.kt)
      and [MultiScreen](modo-compose/src/main/java/com/github/terrakok/modo/multiscreen/MultiScreenActions.kt)
* There are Screen and ContainerScreen
    * ContainerScreen can contains nad render child screens
    * There are some build-in implementation of ContainerScreen like StackScreen and MultiScreen
* You can easily create custom `Action` by extending `Action` or `ReducerAction`,

![](media/modo_udf.png)

# Quick start

The best way to lear the library is discovering [sample application](sample). You can add Modo into your project doing following steps

1. Add gradle dependency. In your `build.gradle.kts`
    ```kotlin
    plugins {
        //...
        //for serialization screens
        id("kotlin-parcelize")
    }
    
    dependencies {
        implementation("com.github.terrakok:modo-compose:${latest_version}")
    }
    ```
2. Create your first screen. Generally it's convenient to use `StackScreen` as the first screen in the hierarchy:
    ```kotlin
    @Parcelize
    class SampleStack(
        private val stackNavModel: StackNavModel
    ) : StackScreen(stackNavModel) {
    
        constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))
    
        @Composable
        override fun Content(modifier: Modifier) {
            // Render the last screen from the stack
            TopScreenContent(modifier) { modifier ->
                // Define the animation for changing screens
                SlideTransition(modifier)
            }
        }
    }
    ```
3. Create simple screen
    ```kotlin
    @Parcelize
    class SampleScreen(
        override val screenKey: ScreenKey = generateScreenKey()
    ) : Screen {
    
        @Composable
        override fun Content(modifier: Modifier) {
            Text(text = "Hello world", modifier = modifier.fillMaxSize())
        }
    }
    ```
4. Integrate screen with to your application. You can use `rememberRootScreen()` inside your `Fragment` or `Activity`:
    1. For Activity
        ```kotlin
        class MyActivity : AppCompatActivity() {
           override fun onCreate(savedInstanceState: Bundle?) {
               super.onCreate(savedInstanceState)
               setContent {
                   // Remember root screen using rememberSeaveable under the hood.
                   val rootScreen = rememberRootScreen {
                       SampleStack(MainScreen(1))
                   }
                   rootScreen.Content(modifier = Modifier.fillMaxSize())
               }
           }
        }
        ```
    2. For Fragment
        ```kotlin
        class MyFragment : Fragment() {
           override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
               ComposeView(inflater.context).apply {
                   setContent {
                       val rootScreen = rememberRootScreen {
                           SampleStack(MainScreen(screenIndex = 1, canOpenFragment = true))
                       }
                       rootScreen.Content(modifier = Modifier.fillMaxSize())
                   }
               }
        }
        ```

# List of features

| Feature                             | Status | SampleStack |
|-------------------------------------|--------|-------------|
| Stack navigation                    | ✅      | Sample      |
| Multi screen navigation             | ✅      | Sample      |
| Dialogs                             | ✅      | Sample      |
| Arguments                           | ✅      | Sample      |
| Animation                           | ✅      | Sample      |
| Android lifecycle support           | ✅      | Sample      |
| Android ViewModel support           | ✅      | Sample      |
| Activity and process death handling | ✅      | Sample      |
| Activity and fragment integration   | ✅      | Sample      |

# BuildIn Container Screens

You can implement container screen by your own, but Modo provides several default containers

1. [StackScreen](modo-compose/src/main/java/com/github/terrakok/modo/stack) - stack navigation
   implementation. [Sample of navigation](sample/src/main/java/com/github/terrakok/androidcomposeapp/screens/stack/StackActionsScreen.kt)

   <img src="media/modo_base_navigation.gif" width=200 />
2. [Multiscreen](modo-compose/src/main/java/com/github/terrakok/modo/multiscreen) - multiscreen
   navigation. [Sample of navigation](sample/src/main/java/com/github/terrakok/androidcomposeapp/screens/containers/SampleMultiScreen.kt)

   <img src="media/modo_multiscreen.gif" width=200 />

Check out sample for more.

# License

```
MIT License

Copyright (c) Konstantin Tskhovrebov (@terrakok)
          and Karenkov Igor (@KarenkovID)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
