package com.github.terrakok.modo.androidApp.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.*
import com.github.terrakok.modo.androidApp.App
import com.github.terrakok.modo.androidApp.R
import com.github.terrakok.modo.androidApp.Screens

class CommandsFragment : Fragment(R.layout.fragment_commands) {
    private val modo = App.modo
    private val screenId: Int by lazy { requireArguments().getInt(ARG_ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = screenId.toString()
        view.findViewById<View>(R.id.forwardButton).setOnClickListener {
            modo.forward(Screens.Commands(screenId + 1))
        }
        view.findViewById<View>(R.id.multiForwardButton).setOnClickListener {
            modo.forward(
                Screens.Commands(screenId + 1),
                Screens.Commands(screenId + 2),
                Screens.Commands(screenId + 3)
            )
        }
        view.findViewById<View>(R.id.replaceButton).setOnClickListener {
            modo.replace(Screens.Commands(screenId + 1))
        }
        view.findViewById<View>(R.id.multiReplaceButton).setOnClickListener {
            modo.replace(
                Screens.Commands(screenId + 1),
                Screens.Commands(screenId + 2),
                Screens.Commands(screenId + 3)
            )
        }
        view.findViewById<View>(R.id.newRootButton).setOnClickListener {
            modo.newStack(Screens.Commands(screenId + 1))
        }
        view.findViewById<View>(R.id.newStackButton).setOnClickListener {
            modo.newStack(
                Screens.Commands(screenId + 1),
                Screens.Commands(screenId + 2),
                Screens.Commands(screenId + 3)
            )
        }
        view.findViewById<View>(R.id.backToButton).setOnClickListener {
            modo.backTo(Screens.Commands(3).id)
        }
        view.findViewById<View>(R.id.backButton).setOnClickListener {
            modo.back()
        }
        view.findViewById<View>(R.id.backToRootButton).setOnClickListener {
            modo.backToRoot()
        }
        view.findViewById<View>(R.id.exitButton).setOnClickListener {
            modo.exit()
        }
    }

    companion object {
        private const val ARG_ID = "arg_id"
        fun create(id: Int) = CommandsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ID, id)
            }
        }
    }
}