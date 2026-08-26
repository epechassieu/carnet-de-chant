package fr.epechassieu.carnetdechant.domain.model

/**
 * Available text size presets for the app, expressed as a scale factor
 * applied to the base [fr.epechassieu.carnetdechant.ui.theme.AppTypography].
 */
enum class TextSize(val scale: Float) {
    PETIT(0.85f),
    NORMAL(1f),
    GRAND(1.15f),
    TRES_GRAND(1.3f);

    companion object {
        val Default = NORMAL
    }
}
