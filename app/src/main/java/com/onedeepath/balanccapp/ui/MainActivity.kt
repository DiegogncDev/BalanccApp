package com.onedeepath.balanccapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.onedeepath.balanccapp.ui.navigation.AppNavigation
import com.onedeepath.balanccapp.ui.theme.BalanccAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BalanccAppTheme {

                    AppNavigation()

            }
        }
    }
}


