package com.github.terrakok.modo.sample.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
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
            val fragment: Fragment = if (intent.getBooleanExtra(EXTRA_USE_LEGACY, false)) {
                ModoLegacyIntegrationFragment()
            } else {
                ModoFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    companion object {
        private const val EXTRA_USE_LEGACY = "EXTRA_USE_LEGACY"

        fun createIntent(context: Context, useLegacy: Boolean = false): Intent =
            Intent(context, ModoFragmentIntegrationActivity::class.java)
                .putExtra(EXTRA_USE_LEGACY, useLegacy)
    }
}