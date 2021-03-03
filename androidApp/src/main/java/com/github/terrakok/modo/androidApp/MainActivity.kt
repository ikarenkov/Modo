package com.github.terrakok.modo.androidApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.back

class MainActivity : AppCompatActivity() {
    private val modo = App.INSTANCE.modo

    //must be lazy otherwise initialization fails with early access to fragment manager
    private val modoRender by lazy {
        object : ModoRender(this@MainActivity, R.id.container) {
            //only for sample
            override fun invoke(state: NavigationState) {
                super.invoke(state)
                findViewById<Toolbar>(R.id.toolbar).title = state.chain.joinToString("-") { it.id }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        modo.init(savedInstanceState, modoRender, Screens.Sample(1))
    }

    override fun onResume() {
        super.onResume()
        modo.render = modoRender
    }

    override fun onPause() {
        modo.render = null
        super.onPause()
    }

    override fun onBackPressed() {
        modo.back()
    }
}
