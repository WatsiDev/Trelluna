package com.watsidev.kanbanboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.watsidev.kanbanboard.ui.App
import com.watsidev.kanbanboard.ui.theme.KanbamBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanbamBoardTheme {
                App()
            }
        }
    }
}