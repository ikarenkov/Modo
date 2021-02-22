# Modo
[![Maven Central](https://img.shields.io/maven-central/v/com.github.terrakok/modo)](https://repo1.maven.org/maven2/com/github/terrakok)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Modo is navigation library based on UDF principles for developing Single Activity applications.
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
implementation("com.github.terrakok:modo:${latest_version}")
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
    fun MyScreen() = AppScreen("MyScreen") { MyScreenFragment() }

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
        modo.init(savedInstanceState, modoRender, Screens.MyScreen())
    }

    override fun onResume() {
        super.onResume()
        modo.render = modoRender
    }

    override fun onPause() {
        modo.render = null
        super.onPause()
    }
}
```

4) Use Modo for navigation:
```kotlin
modo.forward(screen) //navigate to next screen
modo.replace(screen) //replace current screen
modo.newStack(screen) //replace current screen stack
modo.backTo(screenId) //back to screen in current stack if exist
modo.back() //back to previous
modo.exit() //exit from activity
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
