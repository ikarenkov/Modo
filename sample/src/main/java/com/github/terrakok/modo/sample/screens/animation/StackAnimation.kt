package com.github.terrakok.modo.sample.screens.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.animation.StackTransitionType

/**
 * Interface for defining custom screen transitions for a stack-based navigation controller.
 *
 * Allows specifying enter and exit transitions for different navigation operations, including pushing, replacing, and popping screens.
 *
 * @see StackTransitionType
 * @see AnimatedContentTransitionScope
 */
interface StackAnimation {

    /**
     * Defines the transition for this and hiding screens when this screen is pushed to the Stack.
     * If this is not null, [getPushEnterTransition] and [getPushExitTransition] are not taken into account.
     */
    fun getPushTransition(animationScope: AnimatedContentTransitionScope<Screen>): ContentTransform? =
        null

    /**
     * Defines the enter transition for this screen when a push operation is detected.
     * The enter transition describes how this screen appears during a push operation.
     * Only used if [getPushTransition] returns null.
     */
    fun getPushEnterTransition(animationScope: AnimatedContentTransitionScope<Screen>): EnterTransition? =
        null

    /**
     * Defines the exit transition for this screen when a push operation is detected.
     * The exit transition describes how the previous screen (this) disappears during a push operation.
     * Only used if [getPushTransition] returns null.
     */
    fun getPushExitTransition(animationScope: AnimatedContentTransitionScope<Screen>): ExitTransition? =
        null

    /**
     * Defines the transition (enter and exit) when this screen replaces another in the Stack.
     * If this is not null, [getReplaceEnterTransition] and [getReplaceExitTransition] are not taken into account.
     */
    fun getReplaceTransition(animationScope: AnimatedContentTransitionScope<Screen>): ContentTransform? =
        null

    /**
     * Defines the enter transition for this screen when it replaces another screen in the Stack.
     * The enter transition describes how this screen appears during a replace operation.
     * Only used if [getReplaceTransition] returns null.
     */
    fun getReplaceEnterTransition(animationScope: AnimatedContentTransitionScope<Screen>): EnterTransition? =
        null

    /**
     * Defines the exit transition for the replaced screen during a replace operation.
     * The exit transition describes how the replaced screen disappears during a replace operation.
     * Only used if [getReplaceTransition] returns null.
     */
    fun getReplaceExitTransition(animationScope: AnimatedContentTransitionScope<Screen>): ExitTransition? =
        null

    /**
     * Defines the transition (enter and exit) when this screen is popped from the Stack.
     * If this is not null, [getPopEnterTransition] and [getPopExitTransition] are not taken into account.
     */
    fun getPopTransition(animationScope: AnimatedContentTransitionScope<Screen>): ContentTransform? =
        null

    /**
     * Defines the enter transition for this screen when the previous screen is popped.
     * The enter transition describes how this screen appears during a pop operation.
     * Only used if [getPopTransition] returns null.
     */
    fun getPopEnterTransition(animationScope: AnimatedContentTransitionScope<Screen>): EnterTransition? =
        null

    /**
     * Defines the exit transition for this screen when it is popped.
     * The exit transition describes how this screen disappears during a pop operation.
     * Only used if [getPopTransition] returns null.
     */
    fun getPopExitTransition(animationScope: AnimatedContentTransitionScope<Screen>): ExitTransition? =
        null
}