package com.github.terrakok.modo.androidApp.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.*
import com.github.terrakok.modo.androidApp.App
import com.github.terrakok.modo.androidApp.R
import com.github.terrakok.modo.androidApp.Screens

class TabFragment : Fragment(R.layout.fragment_tab) {
    private val modo = App.modo
    private val tabId: Int by lazy { requireArguments().getInt(ARG_TAB_ID) }
    private val screenId: Int by lazy { requireArguments().getInt(ARG_ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = screenId.toString()
        view.findViewById<View>(R.id.forwardButton).setOnClickListener {
            modo.forward(Screens.Tab(tabId, screenId + 1))
        }
        view.findViewById<View>(R.id.replaceButton).setOnClickListener {
            modo.replace(Screens.Tab(tabId, screenId + 1))
        }
        view.findViewById<View>(R.id.externalForwardButton).setOnClickListener {
            modo.externalForward(Screens.Start())
        }
        view.findViewById<View>(R.id.changeTabButton).setOnClickListener {
            modo.selectStack((tabId + 1) % 3)
        }
        view.findViewById<View>(R.id.backToTabRootButton).setOnClickListener {
            modo.backToLocalRoot()
        }
        view.findViewById<View>(R.id.backToStartButton).setOnClickListener {
            modo.backTo(Screens.Start().id)
        }
    }

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TAB_ID = "arg_tab_id"
        fun create(tabId: Int, id: Int) = TabFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TAB_ID, tabId)
                putInt(ARG_ID, id)
            }
        }
    }
}