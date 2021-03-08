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
    val json = chainToJson(state.chain).toString()
    bundle.putString("key_modo_state", json)
}

private fun Modo.restoreState(bundle: Bundle) {
    if (bundle.containsKey("key_modo_state")) {
        val json = bundle.getString("key_modo_state")!!
        restore(NavigationState(jsonToChain(JSONObject(json))))
    }
}

private fun chainToJson(chain: List<Screen>): JSONObject = JSONObject().apply {
    put("chain", JSONArray().apply { chain.forEach { put(it.id) } })
    val multiScreens = chain.filterIsInstance<MultiScreen>().map { multiScreen ->
        JSONObject().also { o ->
            o.put("id", multiScreen.id)
            o.put("selectedStack", multiScreen.selectedStack)
            o.put(
                "stacks",
                JSONArray().also { arr ->
                    multiScreen.stacks.forEach { s -> arr.put(chainToJson(s.chain)) }
                }
            )
        }
    }
    put("multi", JSONArray().also { arr ->
        multiScreens.forEach { arr.put(it) }
    })
}

private fun jsonToChain(json: JSONObject): List<Screen> {
    val rawChain = mutableListOf<String>()
    val screenMap = mutableMapOf<String, Screen>()

    val chainArr = json.getJSONArray("chain")
    (0 until chainArr.length()).forEach {
        val s = chainArr[it].toString().asScreen()
        rawChain.add(s.id)
        screenMap[s.id] = s
    }
    val multiArr = json.getJSONArray("multi")
    (0 until multiArr.length()).forEach {
        val ms = jsonToMultiScreen(multiArr.getJSONObject(it))
        screenMap[ms.id] = ms
    }

    return rawChain.map { screenMap[it]!! }
}

private fun jsonToMultiScreen(json: JSONObject): MultiScreen {
    val id = json.getString("id")
    val selectedStack = json.getInt("selectedStack")
    val stacksArr = json.getJSONArray("stacks")
    val stacks: List<NavigationState> = (0 until stacksArr.length()).map {
        NavigationState(jsonToChain(stacksArr.getJSONObject(it)))
    }
    return MultiScreen(id, stacks, selectedStack)
}

private fun String.asScreen() = RestoredScreen(this)
private class RestoredScreen(
    override val id: String
) : Screen