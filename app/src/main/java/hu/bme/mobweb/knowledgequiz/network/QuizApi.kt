package hu.bme.mobweb.knowledgequiz.network

import hu.bme.mobweb.knowledgequiz.model.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApi {
    @GET("/api.php")
    fun getQuestions(
        @Query("amount") amount: Int?,
        @Query("category") category: String?
    ): Call<Response>
}