package com.github.terrakok.modo.androidApp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.forward

class StartFragment : Fragment(R.layout.fragment_start) {
    private val modo get() = App.INSTANCE.modo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.commandsButton).setOnClickListener {
            modo.forward(Screens.Commands(1))
        }
        view.findViewById<View>(R.id.multiButton).setOnClickListener {
            modo.forward(Screens.MultiStack())
        }
        view.findViewById<View>(R.id.githubButton).setOnClickListener {
            modo.launch(Screens.Browser("https://github.com/terrakok/Modo"))
        }
    }
}