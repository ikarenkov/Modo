package com.github.terrakok.modo.android.multi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.android.ModoRender

class StackContainerFragment : Fragment() {
    internal val index by lazy { requireArguments().getInt(ARG_INDEX) }

    private val render by lazy {
        object : ModoRender(childFragmentManager, CONTAINER_ID, {}) {
            override fun createMultiStackFragment(multiScreen: MultiScreen) = MultiStackFragmentImpl()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FrameLayout(requireContext()).apply {
        id = CONTAINER_ID
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as? MultiStackFragmentImpl)?.setRender(index, render)
    }

    override fun onPause() {
        super.onPause()
        (parentFragment as? MultiStackFragmentImpl)?.setRender(index, null)
    }

    fun getCurrentFragment(): Fragment? =
        childFragmentManager.findFragmentById(CONTAINER_ID)

    companion object {
        private const val CONTAINER_ID = 2387
        private const val ARG_INDEX = "arg_index"

        fun create(index: Int) = StackContainerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_INDEX, index)
            }
        }
    }
}