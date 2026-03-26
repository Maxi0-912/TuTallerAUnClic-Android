package com.manuel.tutalleraunclic

import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.manuel.tutalleraunclic.core.navigation.AppNavigation
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.ui.theme.TuTallerAUnClicTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TuTallerApp()
        }
    }

}