package hu.bme.mobweb.knowledgequiz.network

import android.os.Handler
import hu.bme.mobweb.knowledgequiz.model.Response
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private const val SERVICE_URL = "https://opentdb.com"
    private const val QUESTION_AMOUNT = 10

    private val quizApi: QuizApi

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        quizApi = retrofit.create(QuizApi::class.java)
    }

    private fun <T> runCallOnBackgroundThread(call: Call<T>, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        val handler = Handler()
        Thread {
            try {
                val response = call.execute().body()!!
                handler.post { onSuccess(response) }

            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { onError(e) }
            }
        }.start()
    }

    fun getResponse(
        onSuccess: (Response) -> Unit,
        onError: (Throwable) -> Unit,
        category: String? = ""
    ){
        val getQuestionsRequest = quizApi.getQuestions(QUESTION_AMOUNT, category?:"")
        runCallOnBackgroundThread(getQuestionsRequest, onSuccess, onError)
    }
}