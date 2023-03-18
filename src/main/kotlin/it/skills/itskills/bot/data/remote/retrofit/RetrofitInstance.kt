package it.skills.itskills.bot.data.remote.retrofit

import it.skills.itskills.bot.data.remote.Api
import it.skills.itskills.bot.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()

            val requestBuilderForToken = original.newBuilder().method(original.method, original.body)

                .header("Authorization", "Bearer ${Constants.CHAT_GPT_API_TOKEN}")
                .header("Content-Type", "application/json")
            val requestToken = requestBuilderForToken.build()
            chain.proceed(requestToken)
        }
        .readTimeout(2,TimeUnit.MINUTES)
        .writeTimeout(2,TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.MINUTES)
        .callTimeout(2,TimeUnit.MINUTES)
        .build()


    private fun retrofitInstance() =
        Retrofit.Builder().baseUrl(Constants.CHAT_GPT_API).addConverterFactory(GsonConverterFactory.create())
            .client(client).build()

    fun api(): Api {
        return retrofitInstance().create(Api::class.java)
    }

}