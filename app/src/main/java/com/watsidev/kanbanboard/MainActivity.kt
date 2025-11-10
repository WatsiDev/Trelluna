package com.watsidev.kanbanboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.watsidev.kanbanboard.ui.App
import com.watsidev.kanbanboard.ui.MainViewModel
import com.watsidev.kanbanboard.ui.ServerState
import com.watsidev.kanbanboard.ui.theme.KanbamBoardTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val serverState by viewModel.serverState.collectAsStateWithLifecycle()
            splashScreen.setKeepOnScreenCondition {
                serverState == ServerState.InitialLoading
            }
            KanbamBoardTheme {
                App(serverState = serverState)
            }
        }
    }
}