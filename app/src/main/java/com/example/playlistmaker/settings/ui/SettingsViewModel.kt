package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val darkThemeLiveData = MutableLiveData(settingsInteractor.isDarkThemeEnabled())
    fun observeDarkTheme(): LiveData<Boolean> = darkThemeLiveData

    fun onThemeSwitched(isDarkTheme: Boolean) {
        if (darkThemeLiveData.value == isDarkTheme) return
        settingsInteractor.setDarkThemeEnabled(isDarkTheme)
        darkThemeLiveData.value = isDarkTheme
    }

    fun onShareClicked() = sharingInteractor.shareApp()

    fun onSupportClicked() = sharingInteractor.openSupport()

    fun onUserAgreementClicked() = sharingInteractor.openUserAgreement()
}