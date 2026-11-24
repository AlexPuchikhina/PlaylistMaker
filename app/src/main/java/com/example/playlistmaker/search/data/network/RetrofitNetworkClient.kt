package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(
    private val iTunesService: ITunesApiService
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }
        return try {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } catch (e: Exception) {
            Response().apply { resultCode = -1 }
        }
    }
}