package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}