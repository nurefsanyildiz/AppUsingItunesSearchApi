package com.example.myappusingitunessearchapi.models

import java.io.Serializable

data class ItunesResponse (
    val resultCount: Int,
    val results: List<Results>
): Serializable



