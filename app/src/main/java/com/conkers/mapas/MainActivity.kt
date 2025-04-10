package com.conkers.mapas

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.conkers.mapas.api.AppNavigator
import org.osmdroid.config.Configuration


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContent {
            AppNavigator(apiKey = "5b3ce3597851110001cf62483410b16241c145f69948237a5fc678f6", context = this)
        }
    }
}


