package com.practice.gameapp.data.repositories.network.game

import com.practice.gameapp.data.repositories.models.APIGameModel

class GameAPIClient (
    private val gameAPI: GameAPI
) : GameClient {

    //Fetches all the games from Response and returns a list
    override suspend fun fetchGames(): List<APIGameModel> {
        val gameAPIResponse = gameAPI.getGames()
        if (!gameAPIResponse.isSuccessful) {
            //Exception handling
        }
        return gameAPIResponse.body() ?: emptyList()
    }
}