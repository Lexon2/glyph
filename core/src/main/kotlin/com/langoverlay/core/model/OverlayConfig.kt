package com.langoverlay.core.model

data class OverlayConfig(
    val anchorX: Float = 0.95f,
    val anchorY: Float = 0.05f,
    val opacity: Float = DEFAULT_OPACITY,
    val fontSizeSp: Float = DEFAULT_FONT_SIZE_SP,
) {
    init {
        require(anchorX in 0f..1f) { "anchorX must be in 0..1" }
        require(anchorY in 0f..1f) { "anchorY must be in 0..1" }
        require(opacity in MIN_OPACITY..1f) { "opacity must be in $MIN_OPACITY..1" }
        require(fontSizeSp in 10f..32f) { "fontSizeSp must be in 10..32" }
    }

    companion object {
        const val MIN_OPACITY = 0.4f
        const val DEFAULT_OPACITY = 0.85f
        const val DEFAULT_FONT_SIZE_SP = 14f
    }
}
