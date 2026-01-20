package com.onedeepath.balanccapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.onedeepath.balanccapp.data.datastore.SettingsPreferences
import com.onedeepath.balanccapp.ui.navigation.AppNavigation
import com.onedeepath.balanccapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsPreferences = SettingsPreferences(this)

        // Start save language
        lifecycleScope.launch {
            settingsPreferences.languageCode.first().let { langCode ->
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }

        setContent {

            val isDarkTheme by settingsPreferences.isDarkMode.collectAsState(initial = false)

            AppTheme(darkTheme = isDarkTheme) {

                    AppNavigation(settingsPreferences, isDarkTheme)
            }
        }
    }
}


