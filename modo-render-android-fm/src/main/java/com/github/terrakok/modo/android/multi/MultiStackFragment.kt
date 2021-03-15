package com.github.terrakok.modo.android.multi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.android.MultiStackFragment

open class MultiStackFragmentImpl : MultiStackFragment() {
    private var multiScreen: MultiScreen? = null
        set(value) {
            if (value != null) {
                if (field == null) {
                    view?.findViewById<LinearLayout>(TAB_CONTAINER_ID)?.let {
                        createTabs(value, it)
                    }
                }

                field = value
                localRenders.forEach { (index, render) ->
                    value.stacks.getOrNull(index)?.let { state ->
                        render(state)
                    }
                }
                selectTab(value.selectedStack)
            }
        }

    private val localRenders = mutableMapOf<Int, NavigationRender>()
    internal fun setRender(index: Int, render: NavigationRender?) {
        if (render != null) {
            localRenders[index] = render
            multiScreen?.stacks?.getOrNull(index)?.let { state ->
                render(state)
            }
        } else {
            localRenders.remove(index)
        }
    }

    override fun applyMultiState(multiScreen: MultiScreen) {
        this.multiScreen = multiScreen
    }

    override fun getCurrentFragment(): Fragment? =
        childFragmentManager.fragments
            .filterIsInstance<StackContainerFragment>()
            .firstOrNull { it.isVisible }
            ?.getCurrentFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LinearLayout(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(container!!.layoutParams).apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
            orientation = LinearLayout.VERTICAL
        }

        val linearLayout = this
        val frame = FrameLayout(requireContext()).apply {
            id = CONTAINER_ID
            layoutParams = LinearLayout.LayoutParams(linearLayout.layoutParams).apply {
                height = 0
                width = ViewGroup.LayoutParams.MATCH_PARENT
                weight = 1F
            }
        }
        addView(frame)

        val tabContainer = LinearLayout(requireContext()).apply {
            id = TAB_CONTAINER_ID
            layoutParams = LinearLayout.LayoutParams(linearLayout.layoutParams).apply {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        addView(tabContainer)
        multiScreen?.let { createTabs(it, tabContainer) }
    }

    private fun createTabs(state: MultiScreen, container: LinearLayout) {
        container.removeAllViews()
        for (i in state.stacks.indices) {
            createTabView(i, container)?.apply {
                layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
                    width = 0
                    weight = 1F
                }
                isSelected = i == state.selectedStack
                container.addView(this)
            }
        }
    }

    protected open fun createTabView(index: Int, parent: LinearLayout): View? = null

    private fun selectTab(index: Int) {
        view?.findViewById<LinearLayout>(TAB_CONTAINER_ID)?.let { tabContainer ->
            for (i in 0 until tabContainer.childCount) {
                tabContainer.getChildAt(i).isSelected = i == index
            }
        }

        val addedFragments =
            childFragmentManager.fragments.filterIsInstance<StackContainerFragment>()

        val currentContainerFragment = addedFragments.firstOrNull { it.isVisible }
        if (currentContainerFragment?.index == index) return

        childFragmentManager.beginTransaction().also { transaction ->
            val tabExists = addedFragments.any { it.index == index }
            if (!tabExists) {
                transaction.add(
                    CONTAINER_ID,
                    StackContainerFragment.create(index),
                    index.toString()
                )
            }
            addedFragments.forEach { f ->
                if (f.index == index && !f.isVisible) {
                    transaction.show(f)
                } else if (f.index != index && f.isVisible) {
                    transaction.hide(f)
                }
            }
        }.commitNowAllowingStateLoss()
    }

    companion object {
        private const val CONTAINER_ID = 9283
        private const val TAB_CONTAINER_ID = 9284
    }
}
