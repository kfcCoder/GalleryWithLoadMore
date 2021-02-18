package com.example.pagergallerypaging.model

import com.example.pagergallerypaging.model.Hit

// old: Pixabay
data class PixabayResponse(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)