package com.deweiwang.bookkeeping.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object OpenAIService {
    fun create(apiKey: String): OpenAIServiceApi {
        val interceptor = Interceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OpenAIServiceApi::class.java)
    }
}

interface OpenAIServiceApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer " // ApiKey
    )
    @POST("v1/chat/completions")
    suspend fun generateChatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val max_tokens: Int = 150,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: Message
)