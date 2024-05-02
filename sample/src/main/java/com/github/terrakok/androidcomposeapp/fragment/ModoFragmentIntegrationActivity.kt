package com.github.terrakok.androidcomposeapp.fragment

import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity

class ModoFragmentIntegrationActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ModoFragment())
                .commit()
        }
    }
}