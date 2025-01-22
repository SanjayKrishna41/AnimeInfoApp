package com.example.animeinfoapp.models

data class Data(
    val episodes: Int,
    val genres: List<Genre>,
    val images: Images,
    val mal_id: Int,
    val popularity: Int,
    val rating: String,
    val score: Int,
    val season: String,
    val source: String,
    val synopsis: String,
    val title: String,
    val trailer: Trailer,
    val url: String,
)