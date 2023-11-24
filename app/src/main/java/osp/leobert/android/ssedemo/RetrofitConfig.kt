package osp.leobert.android.ssedemo

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

/**
 * Classname: RetrofitConfig </p>
 * Description: TODO </p>
 * Created by Leobert on 2023/11/23.
 */
object RetrofitConfig {
    val okhttp = OkHttpClient()
    val retrofit = Retrofit.Builder()
        .callFactory(object :Call.Factory {
            override fun newCall(request: Request): Call {
                TODO("Not yet implemented")
            }

        })
        .addCallAdapterFactory()
        .client(okhttp)
        .build()
}