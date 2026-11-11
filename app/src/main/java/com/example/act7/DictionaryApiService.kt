package com.example.act7

import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordDetails(
        @Path("word") word: String
    ): List<WordResponse>
}