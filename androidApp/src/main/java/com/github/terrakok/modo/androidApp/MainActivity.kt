package com.github.terrakok.modo.androidApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.terrakok.modo.ModoRender
import com.github.terrakok.modo.back
import com.github.terrakok.modo.init
import com.github.terrakok.modo.saveState


class MainActivity : AppCompatActivity() {
    private val modo = App.INSTANCE.modo

    //must be lazy otherwise initialization fails with early access to fragment magaer
    private val modoRender by lazy { ModoRender(this, R.id.container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        modo.init(savedInstanceState, Screens.Sample(1))
    }

    override fun onResume() {
        super.onResume()
        modo.render = {
            modoRender(it)
            //only for sample
            findViewById<Toolbar>(R.id.toolbar).title = it.chain.joinToString("-") { it.id }
        }
    }

    override fun onPause() {
        modo.render = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        modo.saveState(outState) //for resume state after death. without it will be showed first screen
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        modo.back()
    }
}
