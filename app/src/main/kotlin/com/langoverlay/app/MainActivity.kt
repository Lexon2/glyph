package com.langoverlay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.langoverlay.app.ui.onboarding.OnboardingScreen
import com.langoverlay.app.ui.settings.SettingsScreen
import com.langoverlay.app.ui.settings.SettingsViewModel
import com.langoverlay.app.ui.theme.LangOverlayTheme
import com.langoverlay.app.util.NotificationHelper
import com.langoverlay.app.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.ensureChannels(this)
        enableEdgeToEdge()

        setContent {
            LangOverlayTheme {
                val navController = rememberNavController()
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshPermissions()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                val startDestination = if (uiState.settings.onboardingCompleted) {
                    Routes.SETTINGS
                } else {
                    Routes.ONBOARDING
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Routes.ONBOARDING) {
                        OnboardingScreen(
                            onComplete = {
                                navController.navigate(Routes.SETTINGS) {
                                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                                }
                            },
                        )
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.isAccessibilityServiceEnabled(this)) {
            NotificationHelper.showAccessibilityDisabledNotification(this)
        }
    }
}

private object Routes {
    const val ONBOARDING = "onboarding"
    const val SETTINGS = "settings"
}
