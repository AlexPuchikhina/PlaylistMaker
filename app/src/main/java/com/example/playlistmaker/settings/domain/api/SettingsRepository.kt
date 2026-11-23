package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}