package com.langoverlay.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.langoverlay.app.R
import com.langoverlay.core.model.LanguageCatalog
import com.langoverlay.core.model.LanguageEntry
import com.langoverlay.core.model.LanguageListCodec
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageManagerSection(
    languages: List<LanguageEntry>,
    onLanguagesChanged: (List<LanguageEntry>) -> Unit,
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<LanguageEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Text(
        text = stringResource(R.string.settings_languages_order_hint),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val updated = languages.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        onLanguagesChanged(updated)
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = (languages.size * 72).dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(languages, key = { it.id }) { entry ->
            ReorderableItem(reorderState, key = entry.id) { isDragging ->
                LanguageRow(
                    entry = entry,
                    isDragging = isDragging,
                    canRemove = languages.size > 2,
                    onRename = { renameTarget = entry },
                    onRemove = {
                        if (languages.size > 2) {
                            onLanguagesChanged(languages.filterNot { it.id == entry.id })
                        }
                    },
                    dragModifier = Modifier.draggableHandle(),
                )
            }
        }
    }

    TextButton(
        onClick = { showAddSheet = true },
        enabled = languages.size < LanguageListCodec.MAX_LANGUAGES,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.settings_languages_add))
    }

    if (showAddSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = {
                showAddSheet = false
                searchQuery = ""
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_languages_add),
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.settings_languages_search)) },
                    singleLine = true,
                )
                val existingIds = languages.map { it.id }.toSet()
                val candidates = LanguageCatalog.all()
                    .filter { it.id !in existingIds }
                    .filter {
                        searchQuery.isBlank() ||
                            it.displayLabel.contains(searchQuery, ignoreCase = true) ||
                            LanguageCatalog.localizedName(it.id).contains(searchQuery, ignoreCase = true)
                    }
                candidates.forEach { candidate ->
                    TextButton(
                        onClick = {
                            onLanguagesChanged(languages + candidate)
                            showAddSheet = false
                            searchQuery = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            stringResource(
                                R.string.settings_languages_catalog_name,
                                candidate.displayLabel,
                                LanguageCatalog.localizedName(candidate.id),
                            ),
                        )
                    }
                }
            }
        }
    }

    renameTarget?.let { target ->
        var label by remember(target.id) { mutableStateOf(target.displayLabel) }
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text(stringResource(R.string.settings_languages_rename)) },
            text = {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.dialog_rename_label)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = label.trim()
                        if (trimmed.isNotEmpty()) {
                            onLanguagesChanged(
                                languages.map {
                                    if (it.id == target.id) it.copy(displayLabel = trimmed) else it
                                },
                            )
                        }
                        renameTarget = null
                    },
                ) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            },
        )
    }
}

@Composable
private fun LanguageRow(
    entry: LanguageEntry,
    isDragging: Boolean,
    canRemove: Boolean,
    onRename: () -> Unit,
    onRemove: () -> Unit,
    dragModifier: Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = null,
            modifier = dragModifier,
            tint = if (isDragging) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.displayLabel, style = MaterialTheme.typography.titleSmall)
            Text(
                text = LanguageCatalog.localizedName(entry.id),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRename) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.settings_languages_rename))
        }
        IconButton(onClick = onRemove, enabled = canRemove) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.settings_languages_remove))
        }
    }
}

@Composable
fun OverlayVisibilitySelector(
    selected: com.langoverlay.core.model.OverlayVisibilityMode,
    onSelected: (com.langoverlay.core.model.OverlayVisibilityMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        com.langoverlay.core.model.OverlayVisibilityMode.entries.forEach { mode ->
            FilterChip(
                selected = mode == selected,
                onClick = { onSelected(mode) },
                label = {
                    Text(
                        when (mode) {
                            com.langoverlay.core.model.OverlayVisibilityMode.AUTO ->
                                stringResource(R.string.overlay_visibility_auto)
                            com.langoverlay.core.model.OverlayVisibilityMode.ALWAYS ->
                                stringResource(R.string.overlay_visibility_always)
                        },
                    )
                },
            )
        }
    }
}
