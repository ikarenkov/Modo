# Quick Start Guide

<list columns="2">
<li>
From this tutorial you will learn how to start using Modo. You will learn how to:

* Create `Screen` and integrate it in an activity
* Perform basic navigation operations
* Pass arguments to screens
* Customize `StackScreen` animation and content

At the end of this tutorial, you will have a simple application with a single screen and basic navigation.
</li>
<li>
<img src="final_result.png" alt="final_result"/>
</li>
</list>

## Before you start

Make sure that:

- You have Android Studio and Android sdk installed
- You know basics in the Android development like creating activities and fragments and can launch an application.

## Build your first Modo application with a single Screen

### Setup dependencies

In your module-level `build.gradle(.kts)` file, add the Modo Compose dependency:

```Kotlin
dependencies {
    implementation("%latest_modo_compose_dependency%")
}
```

### Create your first Screen

To display UI using Modo, you need to create a `Screen` implementation. Let's create a simple `QuickStartScreen` and implement this step-by-step:

1. Create a new Kotlin file `QuickStartScreen.kt`.
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

3. Implement the `Parcelable` interface for your `Screen`. It's needed to support saving and restoring screens structure. You can easily do it by
   using <tooltip term="parcelize">parcelable</tooltip> [gradle plugin](https://developer.android.com/kotlin/parcelize) and `@Parcelable` annotation:
    ```kotlin
    @Parcelize
    class QuickStartScreen(
        ...
    ) : Screen { ... }
    ```

4. Implement the `screenKey` property using function `generateScreenKey()`. It's crucial to use this function because it generates a unique key for
   each screen:
    ```kotlin
    @Parcelize
    class QuickStartScreen(
        // You need to generate a unique screen key using special function
        override val screenKey: ScreenKey = generateScreenKey()
    ) : Screen { ... }
    ```

Complete code:

```Kotlin
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

### Integrate Screen in the Activity

We've already created `QuickStartScreen`. Now let's integrate it into an activity.

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
2. Integrate the `QuickStartScreen` into the activity. To do so, use `rememberRootScreen` function to create a root screen that will manage
   navigation:
    ```kotlin
    setContent {
        val rootScreen = rememberRootScreen { QuickStartScreen() }
        // TODO: display the root screen
    }
    ```
3. Display the root screen using `Content` function of the root screen:
    ```kotlin
    setContent {
        val rootScreen = rememberRootScreen { QuickStartScreen() }
        // Pass fillMaxSize modifier to display it in full screen 
        rootScreen.Content(Modifier.fillMaxSize())
    }
    ```

Complete code:

```Kotlin
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

### Run the application!

![quick_start_1_result.png](quick_start_1_result.png){ height=400 }

## Perform basic navigation operations

To perform navigation let's use a `StackScreen`. It's a container screen that can contain multiple child screens and renders the top one.

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

2. Let's create `QuickStartScreenContent`, with the title and the button that will navigate to another screen:
    ```kotlin
    @Composable
    private fun QuickStartScreenContent(
        modifier: Modifier,
        openNextScreen: () -> Unit,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
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
3. To navigate to the next screen, we need to retrieve the `StackNavContainer`. You can take the nearest `StackNavContainer` from the `Content`
   function using the `LocalStackNavigation` composition local. In our case it's going be `DefaultStackScreen` that we defined before:
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
   <note title="Back handling">Back handling is automatically provided by `StackScreen`. You can manually navigate back using <code>stackNavigation.back()</code></note>

Complete code:

```Kotlin
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
        horizontalAlignment = CenterHorizontally
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

```Kotlin
class QuickStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Remember root screen using rememberSeaveable under the hood.
            val rootScreen = rememberRootScreen {
                DefaultStackScreen(StackNavModel(QuickStartScreen()))
            }
            rootScreen.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
```

{collapsible="true" default-state="collapsed" collapsed-title="QuickStartActivity.kt"}

## Pass arguments to Screens

To pass arguments to the screen, you can use the constructor argument.

1. Let's add a screen index to the `QuickStartScreen`:

   ```Kotlin
   @Parcelize
   class QuickStartScreen(
       private val screenIndex: Int,
       override val screenKey: ScreenKey = generateScreenKey()
   ) : Screen {
       ...
   }
   ```

2. Use the `screenIndex` argument in the `Content` function to display it:

   ```Kotlin
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

   ```Kotlin
   @Composable
   private fun QuickStartScreenContent(
       modifier: Modifier,
       screenIndex: Int,
       openNextScreen: () -> Unit,
   ) {
       Column(
           modifier = modifier,
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = CenterHorizontally
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

## Customize StackScreen animation and Content

Before we used build-in `DefaultStackScreen` that implement `StackScreen` and provides default animation and content. Let's take
a `DefaultStackScreen` as a starting point and customise it

### Create a custom StackScreen

Copy the `DefaultStackScreen` implementation and change it name to  `QuickStartStackScreen`:

```Kotlin
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

{ collapsible="true" include-lines="" default-state="collapsed" collapsed-title-line-number="2"}

Everything is quite similar to declaring a `Screen`, let's lock at differences:

* `private val navModel: StackNavModel` in the constructor is used to store and update the state of the navigation. Declaring it as constructor
  parameter allows to save and restore it using `@Parcelize` annotation. Don't forget to pass it to the `StackScreen`'s constructor.
* `TopScreenContent(modifier)` is used to render the last screen in the stack.
* `ScreenTransition(modifier)` inside `TopScreenContent` is used to animate the transition between screens.

### Custom animation

1. Let's customize animation a little bit. You can do it by passing `transitionSpec` as a `ScreenTransition`'s parameter:

   ```Kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="9-11" }

2. Inside `transitionSpec` lambda we have access to `ComposeRendererScope` that contains the old and the new states.
   Let's use build-in `calculateStackTransitionType` to determine the <tooltip term="StackTransitionType">StackTransitionType</tooltip>:

   ```Kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="11-12" }

3. Now we can use `StackTransitionType` to define the animation. Let's use `slideInHorizontally` and
   `slideOutHorizontally` with different parameters for the forward and the back transition:

   ```Kotlin
   ```
   { src="QuickStartStackScreen.kt" include-lines="12-24" }

### Customise QuickStartStackScreen content

You can customise `Content` as a regular Composable fun. Let's modify `QuickStartStackScreen`. Draw the background and make screens render with
rounded corners:

1. You can pass modifier to the `TopScreenContent` function to change it:

   ```Kotlin
   ```
   { src="QuickStartStackScreenContentModification.kt" include-lines="9-16,34"}

2. You also can put the `TopScreenContent` inside the other container to customise it. Let's put it inside the `Box` and add buttons to close
   activity:

   ```Kotlin
   ```
   { src="QuickStartStackScreenContentModification.kt" include-lines="8,9,16,34,35-49"}

Complete code:

```Kotlin
```

{src="QuickStartStackScreenContentModification.kt" collapsible="true" default-state="collapsed" collapsed-title-line-number="2"}

## What you've learned {id="what-learned"}

From this tutorial you've learned how to build a simple application with Modo and customise it for your needs. For the further learning, you can
explore [Sample app](%github_code_url%sample).

<seealso>
</seealso>
