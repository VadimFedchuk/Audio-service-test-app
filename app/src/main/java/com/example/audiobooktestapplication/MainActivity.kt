package com.example.audiobooktestapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audiobooktestapplication.ui.screen.PlayerScreen
import com.example.audiobooktestapplication.ui.theme.AudioBookTestApplicationTheme
import com.example.audiobooktestapplication.viewModel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioBookTestApplicationTheme {
                val navController = rememberNavController()
                Scaffold { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "playerScreen",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(route = "playerScreen") {
                            viewModel = hiltViewModel<PlayerViewModel>()
                            val state = viewModel.state.collectAsState().value
                            PlayerScreen(
                                state = state,
                                onEvent = { event -> viewModel.onEvent(event) }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (::viewModel.isInitialized) {
            viewModel.closeService()
        }
        super.onDestroy()
    }
}