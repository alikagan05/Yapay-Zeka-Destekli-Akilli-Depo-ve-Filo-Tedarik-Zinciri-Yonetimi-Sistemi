package com.logisticspro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.logisticspro.app.ui.screens.AiChatScreen
import com.logisticspro.app.ui.screens.HomeScreen
import com.logisticspro.app.ui.screens.LoginScreen
import com.logisticspro.app.ui.screens.fleet.FleetDashboardScreen
import com.logisticspro.app.ui.theme.LogisticsProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogisticsProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable(
                            "login",
                            enterTransition = { androidx.compose.animation.fadeIn() },
                            exitTransition = { androidx.compose.animation.fadeOut() }
                        ) {
                            LoginScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            "home",
                            enterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { it }) + androidx.compose.animation.fadeIn() }
                        ) {
                            HomeScreen(
                                onNavigateToFleet = {
                                    navController.navigate("fleet_dashboard")
                                },
                                onNavigateToChat = {
                                    navController.navigate("ai_chat")
                                }
                            )
                        }
                        composable("fleet_dashboard") {
                            FleetDashboardScreen()
                        }
                        composable(
                            "ai_chat",
                            enterTransition = {
                                androidx.compose.animation.slideInVertically(initialOffsetY = { it }) +
                                    androidx.compose.animation.fadeIn()
                            },
                            exitTransition = {
                                androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) +
                                    androidx.compose.animation.fadeOut()
                            }
                        ) {
                            AiChatScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
