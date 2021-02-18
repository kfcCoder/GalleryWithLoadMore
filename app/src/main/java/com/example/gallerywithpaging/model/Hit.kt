package com.example.pagergallerypaging.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 要繼承Serializable才能透過Bundle在Fragment間傳遞
 */
data class Hit( // i.e. 一張圖片
    val comments: Int,
    val downloads: Int,
    val favorites: Int,
    val id: Int,
    val imageHeight: Int,
    val imageSize: Int,
    val imageWidth: Int,
    val largeImageURL: String,
    val likes: Int,
    val pageURL: String,
    val previewHeight: Int,
    val previewURL: String,
    val previewWidth: Int,
    val tags: String,
    val type: String,
    val user: String,
    val userImageURL: String,
    val user_id: Int,
    val views: Int,
    val webformatHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int
) : Serializable