package osp.leobert.android.ssedemo

import okhttp3.Request
import okhttp3.sse.EventSource
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Classname: SseCallAdapter </p>
 * Description: TODO </p>
 * Created by Leobert on 2023/11/23.
 */
class SseCallAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        TODO("Not yet implemented")
    }
}

class SseCall:Call<String> {
    override fun clone(): Call<String> {
        TODO("Not yet implemented")
    }

    override fun execute(): Response<String> {
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun request(): Request {
        TODO("Not yet implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }

    override fun enqueue(callback: Callback<String>) {
        TODO("Not yet implemented")
    }
}

