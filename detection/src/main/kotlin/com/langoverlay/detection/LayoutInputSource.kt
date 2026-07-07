package com.langoverlay.detection

import com.langoverlay.core.model.LayoutInput
import kotlinx.coroutines.flow.Flow

interface LayoutInputSource {
    val inputs: Flow<LayoutInput>
}
