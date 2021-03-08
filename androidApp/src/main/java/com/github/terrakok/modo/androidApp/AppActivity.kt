package com.github.terrakok.modo.androidApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.multi.MultiStackFragmentImpl
import com.github.terrakok.modo.android.saveState

class AppActivity : AppCompatActivity() {
    private val modo = App.modo

    //must be lazy otherwise initialization fails with early access to fragment manager
    private val modoRender by lazy {
        object : ModoRender(this@AppActivity, R.id.container) {
            override fun createMultiStackFragment() = MyMultiStackFragment()

            //only for sample
            override fun invoke(state: NavigationState) {
                super.invoke(state)
                findViewById<TextView>(R.id.title).text =
                    state.chain.joinToString(" - ") { screen ->
                        if (screen is MultiScreen) {
                            val chain = screen.stacks[screen.selectedStack].chain
                                .joinToString(" - ") { it.id }
                            "${screen.id}[$chain]"
                        } else {
                            screen.id
                        }
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        modo.init(savedInstanceState, Screens.Start)
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
}

class MyMultiStackFragment : MultiStackFragmentImpl() {
    private val modo = App.modo
    override fun createTabView(index: Int, parent: LinearLayout): View =
        LayoutInflater.from(context).inflate(R.layout.layout_tab, parent, false).apply {
            findViewById<TextView>(R.id.title).text = "Tab ${index + 1}"
            setOnClickListener {
                val currentScreen = modo.state.chain.lastOrNull()
                if (currentScreen is MultiScreen) {
                    if (currentScreen.selectedStack != index) {
                        modo.selectStack(index)
                    } else {
                        modo.backToTabRoot()
                    }
                }
            }
        }
}
