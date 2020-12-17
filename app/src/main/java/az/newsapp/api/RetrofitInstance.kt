package az.newsapp.api

import az.newsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        // lazy for init when it is used
        // and since this is a singleton
        // it will be created only once
        private val retrofit by lazy {
            // this is just for debug
            val logging = HttpLoggingInterceptor()
            // to log the body
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        // this is the actual single instance
        // the same instance will be used for the whole app
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }

    }
}