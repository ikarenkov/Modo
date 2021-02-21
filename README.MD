# Modo
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

### Usage

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