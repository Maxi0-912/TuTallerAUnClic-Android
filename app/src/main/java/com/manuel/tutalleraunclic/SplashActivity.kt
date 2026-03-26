package com.manuel.tutalleraunclic

import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {

            delay(2000)

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}