package osp.leobert.android.ssedemo

import kotlinx.coroutines.flow.Flow
import retrofit2.Event
import retrofit2.ServerSendEvents
import retrofit2.http.GET

/**
 * Classname: Api
 * Description: TODO
 * Created by Leobert on 2023/11/28.
 */
interface Api {
    @ServerSendEvents
    @GET("http://192.168.8.112:8080/sse/mvc/words")
    fun word(): Flow<Event>

    @GET("http://192.168.8.112:8080/test/words")
    suspend fun test(): Obj
}