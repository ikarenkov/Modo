package io.github.ikarenkov.workshop

import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class WorkShopStackScreen(
    private val navModel: StackNavModel
) : StackScreen(navModel)