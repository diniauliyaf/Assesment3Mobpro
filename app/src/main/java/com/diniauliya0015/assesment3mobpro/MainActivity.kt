package com.diniauliya0015.assesment3mobpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.diniauliya0015.assesment3mobpro.ui.screen.MainScreen
import com.diniauliya0015.assesment3mobpro.ui.theme.Assesment3MobproTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment3MobproTheme {
                MainScreen()
            }
        }
    }
}
