package com.github.terrakok.modo.android

import android.os.Bundle
import android.util.Log
import com.github.terrakok.modo.*
import org.json.JSONArray
import org.json.JSONObject

private var modoInitialized: Boolean = false
fun Modo.init(bundle: Bundle?, firstScreen: Screen) {
    if (bundle == null) {
        if (!modoInitialized) {
            Log.d("Modo", "Activity first launch")
            modoInitialized = true
        } else {
            Log.d("Modo", "Activity re-launch")
        }
        forward(firstScreen)
    } else {
        if (!modoInitialized) {
            Log.d("Modo", "Activity restored after process death")
            modoInitialized = true
            restoreState(bundle)
        } else {
            Log.d("Modo", "Activity restored after rotation")
            //do nothing
        }
    }
}

fun Modo.saveState(bundle: Bundle) {
    val appScreens = mutableMapOf<String, AppScreen>()
    val json = navStateToJson(state, appScreens).toString()
    bundle.putString("key_modo_state", json)
    bundle.putParcelableArray("key_modo_app_screens", appScreens.values.toTypedArray())
}

private fun Modo.restoreState(bundle: Bundle) {
    if (bundle.containsKey("key_modo_state")) {
        val navState = jsonToNavState(
            JSONObject(bundle.getString("key_modo_state")!!),
            bundle.getParcelableArray("key_modo_app_screens")
                ?.toList()
                .orEmpty()
                .filterIsInstance<AppScreen>()
                .associateBy { it.id }
        )
        restore(navState)
    }
}

private fun navStateToJson(
    navigationState: NavigationState,
    appScreenAccumulator: MutableMap<String, AppScreen>
): JSONObject {
    val chain = navigationState.chain
    val ids = chain.map { it.id }
    val multiScreens = chain.filterIsInstance<MultiScreen>()
    val appScreens = chain.filterIsInstance<AppScreen>()

    appScreens.forEach { appScreenAccumulator[it.id] = it }

    return JSONObject().apply {
        put("chain", JSONArray(ids))
        val multiJson = multiScreens.map { ms ->
            JSONObject().also { o ->
                o.put("id", ms.id)
                o.put("selectedStack", ms.selectedStack)
                o.put("stacks", JSONArray().also { arr ->
                    ms.stacks.forEach { s -> arr.put(navStateToJson(s, appScreenAccumulator)) }
                }
                )
            }
        }
        put("multi", JSONArray(multiJson))
    }
}

private fun jsonToNavState(json: JSONObject, appScreens: Map<String, AppScreen>): NavigationState {
    val rawChain = mutableListOf<String>()
    val chainArr = json.getJSONArray("chain")
    (0 until chainArr.length()).forEach {
        rawChain.add(chainArr[it].toString())
    }

    val multiMap = mutableMapOf<String, MultiScreen>()
    val multiArr = json.getJSONArray("multi")
    (0 until multiArr.length()).forEach {
        val ms = jsonToMultiScreen(multiArr.getJSONObject(it), appScreens)
        multiMap[ms.id] = ms
    }

    return NavigationState(
        rawChain.map { id -> appScreens[id] ?: multiMap[id] ?: error("Unknown screen ID=$id") }
    )
}

private fun jsonToMultiScreen(json: JSONObject, appScreens: Map<String, AppScreen>): MultiScreen {
    val id = json.getString("id")
    val selectedStack = json.getInt("selectedStack")
    val stacksArr = json.getJSONArray("stacks")
    val stacks: List<NavigationState> = (0 until stacksArr.length()).map { i ->
        jsonToNavState(stacksArr.getJSONObject(i), appScreens)
    }
    return MultiScreen(id, stacks, selectedStack)
}
