package com.langoverlay.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.langoverlay.app.R
import com.langoverlay.app.ui.settings.SettingsViewModel
import com.langoverlay.app.util.PermissionUtils

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val step = when {
        !uiState.accessibilityEnabled -> OnboardingStep.Accessibility
        !uiState.overlayGranted -> OnboardingStep.Overlay
        else -> OnboardingStep.Battery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.onboarding_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        when (step) {
            OnboardingStep.Accessibility -> {
                Text(stringResource(R.string.onboarding_accessibility_body))
                if (PermissionUtils.needsRestrictedSettingsHint()) {
                    Text(
                        text = stringResource(R.string.onboarding_restricted_settings),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                    OutlinedButton(
                        onClick = { context.startActivity(PermissionUtils.appDetailsIntent(context)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.onboarding_open_app_info))
                    }
                }
                Button(
                    onClick = { context.startActivity(PermissionUtils.accessibilitySettingsIntent()) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.onboarding_enable_accessibility))
                }
            }

            OnboardingStep.Overlay -> {
                Text(stringResource(R.string.onboarding_overlay_body))
                Button(
                    onClick = { context.startActivity(PermissionUtils.overlaySettingsIntent(context)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.onboarding_grant_overlay))
                }
            }

            OnboardingStep.Battery -> {
                Text(stringResource(R.string.onboarding_battery_body))
                Button(
                    onClick = { context.startActivity(PermissionUtils.batteryOptimizationIntent(context)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.onboarding_battery_button))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.onboarding_finish))
                }
            }
        }

        OutlinedButton(
            onClick = { viewModel.refreshPermissions() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboarding_refresh))
        }
    }
}

private enum class OnboardingStep {
    Accessibility,
    Overlay,
    Battery,
}
