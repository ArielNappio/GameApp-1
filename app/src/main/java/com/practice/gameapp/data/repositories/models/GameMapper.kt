package com.practice.gameapp.domain.models

import com.practice.gameapp.data.repositories.models.APIGameModel

fun APIGameModel.toGame() = GameModel(
    title = title,
    platform = platform,
    genre = genre,
    short_description = ShortDescription,
    thumbnail = thumbnail
)