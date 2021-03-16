# Modo
[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Modo is navigation library based on UDF principles for developing Single Activity applications.

<table>
    <tr>
        <td>
            <img src="https://github.com/terrakok/Modo/raw/master/media/1.gif"/>
        </td>
        <td>
            <img src="https://github.com/terrakok/Modo/raw/master/media/2.gif"/>
        </td>
        <td>
            <img src="https://github.com/terrakok/Modo/raw/master/media/3.gif"/>
        </td>
    </tr>
    <tr>
        <td>
            Power navigation
        </td>
        <td>
            Multibackstack
        </td>
        <td>
            Launch external activities
        </td>
    </tr>
</table>

```
 Modo                                                  Activity
 +---------------------------------------------+       +----------------------+
 |                                             |       |                      |
 |         +---------------------------------+ |       |                      |
 |         |                                 | |       |                      |
 |         \/          +-----------------+   | |       |                      |
 |  NavigationState--->|                 |   | |       |  +----------------+  |
 |                     |NavigationReducer|---+-|-------|->|NavigationRender|  |
 |             +------>|                 |     |       |  +----------------+  |
 |             |       +-----------------+     |       |                      |
 +---------------------------------------------+       +----------------------+
               |                                                   |
               |       +----------------+                          |
               +-------|NavigationAction|<-------------------------+
                       +----------------+
```

## Download
```kotlin
plugins {
  //...
  //for serialization screens
  id("kotlin-parcelize")
}

dependencies {
  //...
  //modo core
  implementation("com.github.terrakok:modo:${latest_version}")
  //for navigation based on FragmentManager
  implementation("com.github.terrakok:modo-render-android-fm:${latest_version}")
}
```

## Usage
1) Init Modo instance:
```kotlin
class App : Application() {
    val modo = Modo(AppReducer(this))
}
```

2) Describe your screens:
```kotlin
object Screens {
  @Parcelize
  class Start : AppScreen("Start") {
    override fun create() = StartFragment()
  }

  fun Browser(url: String) = ExternalScreen {
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
  }
}
```

3) Setup your application activity:
```kotlin
class MainActivity : AppCompatActivity() {
  private val modo = App.INSTANCE.modo

  //must be lazy otherwise initialization fails with early access to fragment manager
  private val modoRender by lazy { ModoRender(this, R.id.container) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    modo.init(savedInstanceState, Screens.Start())
  }

  override fun onResume() {
    super.onResume()
    modo.render = modoRender
  }

  override fun onPause() {
    modo.render = null
    super.onPause()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    //add this if you want to restore app after process death
    modo.saveState(outState)
  }
}
```

4) Use Modo for navigation:
See [CommandsFragment.kt](https://github.com/terrakok/Modo/blob/master/sample-android-fm/src/main/java/com/github/terrakok/modo/androidApp/fragment/CommandsFragment.kt)
for additional examples.
```kotlin
modo.forward(screen) //navigate to next screen
modo.replace(screen) //replace current screen
modo.newStack(screen) //replace current screen stack
modo.backTo(screenId) //back to screen in current stack if exist
modo.back() //back to previous
modo.exit() //exit from activity
```

## Multistack navigation
Modo gives you multistack support out-of-the-box!

1) Use `MultiReducer` for initialization
```kotlin
modo = Modo(LogReducer(AppReducer(this, MultiReducer())))
```

2) Add multistack screen:
```kotlin
object Screens {
  @Parcelize
  class Start : AppScreen("Start") {
    override fun create() = StartFragment()
  }

  @Parcelize
  class MyScreen : AppScreen("MyScreen") {
    override fun create() = MyFragment()
  }
  //other screens...

  fun MultiStack() = MultiAppScreen(
    "MultiStack", //some id
    listOf(Start(), MyScreen()), //root screens in tabs
    1 //selected tab by default
  )
}
```

3) Describe how tab view will be look:
```kotlin
class MyMultiStackFragment : MultiStackFragmentImpl() {
  override fun createTabView(index: Int, parent: LinearLayout): View =
    LayoutInflater.from(context)
      .inflate(R.layout.layout_tab, parent, false)
}
```

4) Put it in your render:
```kotlin
private val modoRender by lazy {
  object : ModoRender(this@AppActivity, R.id.container) {
    override fun createMultiStackFragment() = MyMultiStackFragment()
  }
}
```

5) Just use new available commands!
See [TabFragment.kt](https://github.com/terrakok/Modo/blob/master/sample-android-fm/src/main/java/com/github/terrakok/modo/androidApp/fragment/TabFragment.kt)
for additional examples.
```kotlin
modo.externalForward(Screens.Start()) //open new screen above tabs
modo.selectStack(1) //change tab
modo.backToTabRoot() //return on tab root
```

## Debug
You can use `LogReducer` for logging navigation state changes
```kotlin
Modo(
  if (BuildConfig.DEBUG) LogReducer(AppReducer(this@App))
  else AppReducer(this@App)
)
```
Logcat (from sample application):
```
D/Modo: Activity first launch
D/Modo: New action=com.github.terrakok.modo.Forward@9d0f15d
D/Modo: New state=NavigationState(chain=[[1]])
```

## Samples
Base features are showed in sample app: [sample-android-fm](https://github.com/terrakok/Modo/tree/master/sample-android-fm)

## Extending ModoRender
```kotlin
private val modoRender by lazy {
  object : ModoRender(this@MainActivity, R.id.container) {
    override fun pop(count: Int) {
      hideKeyboard()
      super.pop(count)
    }

    override fun push(screens: List<Screen>) {
      hideKeyboard()
      super.push(screens)
    }

    override fun setupTransaction(
      fragmentManager: FragmentManager,
      transaction: FragmentTransaction,
      screen: AppScreen,
      newFragment: Fragment
    ) {
      //e.g. setup your animation
    }
  }
}

fun Activity.hideKeyboard() {
  currentFocus?.apply {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, 0)
  }
}
```

## License
```
MIT License

Copyright (c) 2021 Konstantin Tskhovrebov (@terrakok)

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
