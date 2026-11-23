package com.example.playlistmaker.player.ui

import com.example.playlistmaker.player.domain.models.PlayerState

data class PlayerScreenState(
    val playerState: PlayerState,
    val progress: String
)