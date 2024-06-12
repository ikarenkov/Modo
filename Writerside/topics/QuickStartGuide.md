Here's the improved version of your documentation text:

# Quick Start Guide

<list columns="2">
<li>
From this tutorial, you will learn how to start using Modo. You will learn how to:

* Create a `Screen` and integrate it into an activity
* Perform basic navigation operations
* Pass arguments to screens
* Customize `StackScreen` animation and content

At the end of this tutorial, you will have a simple application with a single screen and basic navigation.
</li>
<li>
<img src="final_result.png" alt="Final result"/>
</li>
</list>

## Before You Start

Make sure that:

- You have Android Studio and the Android SDK installed
- You know the basics of Android development, such as creating activities and fragments, and can launch an application.

## Build Your First Modo Application with a Single Screen

### Setup Dependencies

In your module-level `build.gradle(.kts)` file, add the Modo Compose dependency:

```kotlin
dependencies {
    implementation("%latest_modo_compose_dependency%")
}
```

### Create Your First Screen

To display UI using Modo, you need to create a `Screen` implementation. Let's create a simple `QuickStartScreen` and implement it step-by-step:

1. Create a new Kotlin file `QuickStartScreen.kt`:
    ```kotlin
    // TODO: provide Parcelable implementation
    class QuickStartScreen(
        // TODO: implement screenKey
    ) : Screen {
         @Composable
         override fun Content(modifier: Modifier) {
             // TODO: implement your UI
         }
    }
    ```

2. Override the `Content` function to provide the UI for your screen. Don't forget to use `modifier` in your root element:
    ```kotlin
    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier = modifier) {
            Text(
                text = "Hello, Modo!",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
    ```

3. Implement the `Parcelable` interface for your `Screen`. It's needed to support saving and restoring the screen's structure. You can easily do it by
   using the <tooltip term="parcelize">parcelable</tooltip> [gradle plugin](https://developer.android.com/kotlin/parcelize) and the `@Parcelize`
   annotation:
    ```kotlin
    @Parcelize
    class QuickStartScreen(
        ...
    ) : Screen { ... }
    ```

4. Implement the `screenKey` property using the `generateScreenKey()` function. It's crucial to use this function because it generates a unique key
   for each screen:
    ```kotlin
    @Parcelize
    class QuickStartScreen(
        // You need to generate a unique screen key using a special function
        override val screenKey: ScreenKey = generateScreenKey()
    ) : Screen { ... }
    ```

Complete code:

```kotlin
@Parcelize
class QuickStartScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier = modifier) {
            Text(
                text = "Hello, Modo!",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
```

{collapsible="true" default-state="collapsed" collapsed-title="QuickStartScreen"}

### Integrate the Screen into the Activity

Now that we've created `QuickStartScreen`, let's integrate it into an activity.

1. Create a new activity `QuickStartActivity` and add the following code:
    ```kotlin
    class QuickStartActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                // TODO: integrate screen
            }
        }
    }
    ```

2. Integrate the `QuickStartScreen` into the activity. To do so, use the `rememberRootScreen` function to create a root screen that will manage
   navigation:
    ```kotlin
    setContent {
        val rootScreen = rememberRootScreen { QuickStartScreen() }
        // TODO: display the root screen
    }
    ```

3. Display the root screen using the `Content` function of the root screen:
    ```kotlin
    setContent {
        val rootScreen = rememberRootScreen { QuickStartScreen() }
        // Pass fillMaxSize modifier to display it in full screen 
        rootScreen.Content(Modifier.fillMaxSize())
    }
    ```

Complete code:

```kotlin
class QuickStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rootScreen = rememberRootScreen {
                QuickStartScreen()
            }
            rootScreen.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
```

{collapsible="true" default-state="collapsed" collapsed-title="QuickStartActivity"}

### Run the Application!

![quick_start_1_result.png](quick_start_1_result.png){ height=400 }

## Perform Basic Navigation Operations

To perform navigation, let's use a `StackScreen`. It's a container screen that can contain multiple child screens and renders the top one.

1. Use the `DefaultStackScreen` in your activity:
    ```kotlin
    setContent {
        val rootScreen = rememberRootScreen {
            DefaultStackScreen(
                // Use StackNavModel to define the initial screen
                StackNavModel(
                    QuickStartScreen()
                )
            )
        }
        rootScreen.Content(modifier = Modifier.fillMaxSize())
    }
    ```

2. Let's create `QuickStartScreenContent`, with a title and a button that will navigate to another screen:
    ```kotlin
    @Composable
    private fun QuickStartScreenContent(
        modifier: Modifier,
        openNextScreen: () -> Unit,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello, Modo!")
            Button(
                onClick = openNextScreen
            ) {
                Text(text = "Next screen")
            }
        }
    }
    ```

3. To navigate to the next screen, we need to retrieve the `StackNavContainer`. You can get the nearest `StackNavContainer` from the `Content`
   function using the `LocalStackNavigation` composition local. In our case, it's going to be `DefaultStackScreen` that we defined earlier:
    ```kotlin
    @Composable
    override fun Content(modifier: Modifier) {
        val stackNavigation = LocalStackNavigation.current
        QuickStartScreenContent(
            modifier = modifier,
            openNextScreen = {
                // TODO: navigate using stackNavigation
            },
        )
    }
    ```

4. To navigate to the next screen, use the `forward` function of the `StackNavContainer` and pass a new screen to it:
    ```kotlin
    QuickStartScreenContent(
        modifier = modifier,
        openNextScreen = {
            stackNavigation.forward(QuickStartScreen())
        },
    )
    ```

5. Launch the application and check the result! Try to navigate to the next screen by clicking the button and navigate back using the back button.
   <note title="Back Handling">Back handling is automatically provided by <code>StackScreen</code>. You can manually navigate back using <code>stackNavigation.back()</code></note>

Complete code:

```kotlin
@Parcelize
class QuickStartScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val stackNavigation = LocalStackNavigation.current
        QuickStartScreenContent(
            modifier = modifier,
            openNextScreen = {
                stackNavigation.forward(QuickStartScreen())
            },
        )
    }
}

@Composable
private fun QuickStartScreenContent(
    modifier: Modifier,
    openNextScreen: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Hello, Modo!")
        Button(
            onClick = openNextScreen
        ) {
            Text(text = "Next screen")
        }
    }
}
```

{collapsible="true" default-state="collapsed" collapsed-title="QuickStartScreen.kt"}

```kotlin
class QuickStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Remember root screen using rememberSaveable under the hood.
            val rootScreen = rememberRootScreen {
                DefaultStackScreen(StackNavModel(QuickStartScreen()))
            }
            rootScreen.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
```

{collapsible="true" default-state="collapsed" collapsed-title="QuickStartActivity.kt"}

## Pass Arguments to Screens

To pass arguments to the screen, you can use the constructor argument.

1. Let's add a screen index to the `QuickStartScreen`:

   ```kotlin
   @Parcelize
   class QuickStartScreen(
       private val screenIndex: Int,
       override val screenKey: ScreenKey = generateScreenKey()
   ) : Screen {
       ...
   }
   ```

2. Use the `screenIndex` argument in the `Content` function to display it:

   ```kotlin
   @Composable
   override fun Content(modifier: Modifier) {
       val stackNavigation = LocalStackNavigation.current
       QuickStartScreenContent(
           modifier = modifier,
           screenIndex = screenIndex,
           openNextScreen = {
               stackNavigation.forward(QuickStartScreen(screenIndex + 1))
           },
       )
   }
   ```

3. Use the `screenIndex` in the `QuickStartScreenContent`:

   ```kotlin
   @Composable
   private fun QuickStartScreenContent(
       modifier: Modifier,
       screenIndex: Int,
       openNextScreen: () -> Unit,
   ) {
       Column(
           modifier = modifier,
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Text(text = "Hello, Modo! Screen №$screenIndex")
           Button(
               onClick = openNextScreen
           ) {
               Text(text = "Next screen")
           }
       }
   }
   ```

## Customize StackScreen Animation and Content

Before, we used the built-in `DefaultStackScreen` that implements `StackScreen` and provides default animation and content. Let's
take `DefaultStackScreen` as a starting point and customize it.

### Create a Custom StackScreen

Copy the `DefaultStackScreen` implementation and change its name to `QuickStartStackScreen`:

```kotlin
@Parcelize
class QuickStartStackScreen(
    private val navModel: StackNavModel
) : StackScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier) { modifier ->
            ScreenTransition(modifier)
        }
    }

}
```

{collapsible="true" include-lines="" default-state="collapsed" collapsed-title-line-number="2"}

Everything is quite similar to declaring a `Screen`. Let's look at the differences:

* `private val navModel: StackNavModel` in the constructor is used to store and update the state of the navigation. Declaring it as a constructor
  parameter allows it to be saved and restored using the `@Parcelize` annotation. Don't forget to pass it to the `StackScreen`'s constructor.
* `TopScreenContent(modifier)` is used to render the last screen in the stack.
* `ScreenTransition(modifier)` inside `TopScreenContent` is used to animate the transition between screens.

### Custom Animation

1. Let's customize the animation a little bit. You can do it by passing `transitionSpec` as a `ScreenTransition` parameter:

   ```kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="9-11" }

2. Inside the `transitionSpec` lambda, we have access to the `ComposeRendererScope` that contains the old and new states. Let's use the
   built-in `calculateStackTransitionType` to determine the <tooltip term="StackTransitionType">StackTransitionType</tooltip>:

   ```kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="11-12" }

3. Now we can use `StackTransitionType` to define the animation. Let's use `slideInHorizontally` and `slideOutHorizontally` with different parameters
   for the forward and back transitions:

   ```kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="12-24" }

### Customize QuickStartStackScreen Content

You can customize `Content` as a regular composable function. Let's modify `QuickStartStackScreen`. Draw the background and make screens render with
rounded corners:

1. You can pass a modifier to the `TopScreenContent` function to change it:

   ```kotlin
   ```
   { src="QuickStartStackScreenContentModification.kt" include-lines="9-16,34"}

2. You can also put the `TopScreenContent` inside another container to customize it. Let's put it inside a `Box` and add buttons to close the
   activity:

   ```kotlin
   ```
   { src="QuickStartStackScreenContentModification.kt" include-lines="8,9,16,34,35-49"}

Complete code:

```kotlin
```

{src="QuickStartStackScreenContentModification.kt" collapsible="true" default-state="collapsed" collapsed-title-line-number="2"}

## What You've Learned {id="what-learned"}

From this tutorial, you've learned how to build a simple application with Modo and customize it for your needs. For further learning, you can explore
the [Sample app](%github_code_url%sample).

<seealso>
</seealso>