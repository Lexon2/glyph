package com.langoverlay.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.langoverlay.app.R
import com.langoverlay.core.model.OverlayAppearance
import com.langoverlay.core.model.OverlayConfig
import com.langoverlay.core.model.ShortcutPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings_title)) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusCard(uiState)

            SectionTitle(stringResource(R.string.settings_languages))
            LanguageManagerSection(
                languages = settings.languages,
                onLanguagesChanged = viewModel::updateLanguages,
            )

            SectionTitle(stringResource(R.string.settings_shortcut))
            ShortcutSelector(
                selected = settings.shortcut,
                onSelected = viewModel::updateShortcut,
            )

            SectionTitle(stringResource(R.string.settings_overlay))
            OverlayAppearanceSelector(
                selected = settings.overlayAppearance,
                onSelected = viewModel::updateAppearance,
            )

            Text(
                text = stringResource(R.string.settings_overlay_visibility),
                style = MaterialTheme.typography.bodyMedium,
            )
            OverlayVisibilitySelector(
                selected = settings.overlayVisibilityMode,
                onSelected = viewModel::updateOverlayVisibility,
            )

            LabeledSlider(
                label = stringResource(R.string.settings_opacity),
                value = settings.overlay.opacity,
                valueRange = OverlayConfig.MIN_OPACITY..1f,
                onValueChange = viewModel::updateOpacity,
            )

            LabeledSlider(
                label = stringResource(R.string.settings_font_size),
                value = settings.overlay.fontSizeSp,
                valueRange = 10f..32f,
                onValueChange = viewModel::updateFontSize,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.settings_start_at_boot))
                Switch(
                    checked = settings.startAtBoot,
                    onCheckedChange = viewModel::updateStartAtBoot,
                )
            }

            OutlinedButton(
                onClick = viewModel::resetOverlayPosition,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_reset_position))
            }
        }
    }
}

@Composable
private fun StatusCard(uiState: SettingsUiState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusRow(
                label = stringResource(R.string.status_accessibility),
                ok = uiState.accessibilityEnabled,
            )
            StatusRow(
                label = stringResource(R.string.status_overlay),
                ok = uiState.overlayGranted,
            )
            StatusRow(
                label = stringResource(R.string.status_service),
                ok = uiState.serviceRunning,
            )
        }
    }
}

@Composable
private fun StatusRow(label: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Text(
            text = if (ok) stringResource(R.string.status_ok) else stringResource(R.string.status_missing),
            color = if (ok) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
    HorizontalDivider()
}

@Composable
private fun ShortcutSelector(
    selected: ShortcutPreset,
    onSelected: (ShortcutPreset) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ShortcutPreset.entries.forEach { preset ->
            FilterChip(
                selected = preset == selected,
                onClick = { onSelected(preset) },
                label = { Text(preset.displayName) },
            )
        }
    }
}

@Composable
private fun OverlayAppearanceSelector(
    selected: OverlayAppearance,
    onSelected: (OverlayAppearance) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OverlayAppearance.entries.forEach { appearance ->
            FilterChip(
                selected = appearance == selected,
                onClick = { onSelected(appearance) },
                label = {
                    Text(
                        when (appearance) {
                            OverlayAppearance.SYSTEM -> stringResource(R.string.appearance_system)
                            OverlayAppearance.LIGHT -> stringResource(R.string.appearance_light)
                            OverlayAppearance.DARK -> stringResource(R.string.appearance_dark)
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Text("$label: ${"%.1f".format(value)}")
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}
