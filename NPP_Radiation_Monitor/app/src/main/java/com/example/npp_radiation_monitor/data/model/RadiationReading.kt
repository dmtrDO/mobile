package com.example.npp_radiation_monitor.data.model

/**
 * Модель вимірювання радіаційного фону з коротким описом та рекомендаціями.
 */
data class RadiationReading(
    val id: Int,
    val value: Float,
    val timestamp: String,
    val status: RadiationStatus = RadiationStatus.fromValue(value)
) {
    val shortDescription: String
        get() = when (status) {
            RadiationStatus.SAFE -> "Природний радіаційний фон у межах безпечної норми."
            RadiationStatus.WARNING -> "Увага: зафіксовано помірне підвищення радіаційного фону."
            RadiationStatus.DANGER -> "НЕБЕЗПЕКА: критичне перевищення гранично допустимого рівня!"
        }

    val detailedRecommendations: String
        get() = when (status) {
            RadiationStatus.SAFE -> "Штатний режим моніторингу. Захисні заходи не потрібні."
            RadiationStatus.WARNING -> "Провести калібрування сенсорів та сповістити старшого зміні."
            RadiationStatus.DANGER -> "Негайно активувати протокол аварійного захисту та безпеки!"
        }
}
