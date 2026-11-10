package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() = externalNavigator.shareApp()

    override fun openSupport() = externalNavigator.openSupport()

    override fun openUserAgreement() = externalNavigator.openUserAgreement()
}