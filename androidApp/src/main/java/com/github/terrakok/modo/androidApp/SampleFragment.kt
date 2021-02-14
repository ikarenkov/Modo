package com.github.terrakok.modo.androidApp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.*

class SampleFragment : Fragment(R.layout.fragment_sample) {
    private val modo = App.INSTANCE.modo
    private val screenId: Int by lazy { arguments!!.getInt(ARG_ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.name).text = "Screen $screenId"
        view.findViewById<View>(R.id.back_command).setOnClickListener {
            modo.back()
        }
        view.findViewById<View>(R.id.forward_command).setOnClickListener {
            modo.forward(Screens.Sample(screenId + 1))
        }
        view.findViewById<View>(R.id.replace_command).setOnClickListener {
            modo.replace(Screens.Sample(screenId + 1))
        }
        view.findViewById<View>(R.id.multi_forward_command).setOnClickListener {
            modo.forward(
                Screens.Sample(screenId + 1),
                Screens.Sample(screenId + 2),
                Screens.Sample(screenId + 3)
            )
        }
        view.findViewById<View>(R.id.new_stack_command).setOnClickListener {
            modo.newStack(
                Screens.Sample(screenId + 1),
                Screens.Sample(screenId + 2),
                Screens.Sample(screenId + 3)
            )
        }
        view.findViewById<View>(R.id.new_root_command).setOnClickListener {
            modo.newStack(Screens.Sample(screenId + 1))
        }
        view.findViewById<View>(R.id.forward_delay_command).setOnClickListener {
            it.handler.postDelayed({
                modo.forward(Screens.Sample(screenId + 1))
            }, 3000)
        }
        view.findViewById<View>(R.id.back_to_command).setOnClickListener {
            modo.backTo(Screens.Sample(3).id)
        }
        view.findViewById<View>(R.id.exit_command).setOnClickListener {
            modo.exit()
        }
    }

    companion object {
        private const val ARG_ID = "arg_id"
        fun create(id: Int) = SampleFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ID, id)
            }
        }
    }
}