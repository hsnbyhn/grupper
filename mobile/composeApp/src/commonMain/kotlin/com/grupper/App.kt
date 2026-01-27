package com.grupper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.grupper.navigation.GrupperNavHost
import com.grupper.ui.theme.GrupperTheme

@Composable
fun App() {
    GrupperTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GrupperNavHost(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
