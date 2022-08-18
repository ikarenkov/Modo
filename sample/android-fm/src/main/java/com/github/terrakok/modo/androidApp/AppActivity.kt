package com.github.terrakok.modo.androidApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.multi.TabViewFactory
import com.github.terrakok.modo.android.saveState
import com.github.terrakok.modo.back
import com.github.terrakok.modo.print
import com.github.terrakok.modo.selectStack

class AppActivity : AppCompatActivity(), TabViewFactory {
    private val modo = App.modo

    //must be lazy otherwise initialization fails with early access to fragment manager
    private val modoRender by lazy {
        object : ModoRender(this@AppActivity, R.id.container) {
            //only for sample
            override fun invoke(state: NavigationState) {
                super.invoke(state)
                val stateStr = state.print()
                findViewById<TextView>(R.id.log).text = stateStr
                findViewById<ScrollView>(R.id.scroll).apply {
                    post { fullScroll(View.FOCUS_DOWN) }
                }
            }

        }
    }

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
        modo.saveState(outState)
    }

    override fun onBackPressed() {
        modo.back()
    }

    override fun createTabView(index: Int, parent: LinearLayout): View =
        LayoutInflater.from(this)
            .inflate(R.layout.layout_tab, parent, false)
            .apply {
                findViewById<TextView>(R.id.title).text = "Tab ${index + 1}"
                setOnClickListener { modo.selectStack(index) }
            }
}
