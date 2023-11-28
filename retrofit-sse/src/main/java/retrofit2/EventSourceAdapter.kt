package retrofit2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import okhttp3.Request
import okhttp3.sse.EventSource
import retrofit2.sse.FlowAdapterEventListener
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Classname: EventSourceAdapter
 * Created by Leobert on 2023/11/27.
 */
interface EventSourceAdapter<T> {
    fun adapt(request: Request, factory: EventSource.Factory): T

    class FlowAdapter : EventSourceAdapter<Flow<Event>> {

        override fun adapt(request: Request, factory: EventSource.Factory): Flow<Event> {

            val channel = Channel<Event>()
            return channel.receiveAsFlow()
                .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly)
                .onSubscription {
                    factory.newEventSource(
                        request, FlowAdapterEventListener(channel)
                    )
                }
        }

        companion object {

            val Factory = object : Factory() {
                override fun get(
                    returnType: Type,
                    annotations: Array<Annotation>,
                    retrofitSSE: RetrofitSSE
                ): EventSourceAdapter<*>? {
                    if (Flow::class.java.isAssignableFrom(getRawType(returnType))) {
                        return FlowAdapter()
                    }
                    return null
                }

            }
        }
    }

    abstract class Factory {
        /**
         * Returns a call adapter for interface methods that return `returnType`, or null if it
         * cannot be handled by this factory.
         */
        abstract operator fun get(
            returnType: Type, annotations: Array<Annotation>, retrofitSSE: RetrofitSSE
        ): EventSourceAdapter<*>?

        companion object {
            /**
             * Extract the upper bound of the generic parameter at `index` from `type`. For
             * example, index 1 of `Map<String, ? extends Runnable>` returns `Runnable`.
             */
            protected fun getParameterUpperBound(index: Int, type: ParameterizedType): Type {
                return Utils.getParameterUpperBound(index, type)
            }

            /**
             * Extract the raw class type from `type`. For example, the type representing `List<? extends Runnable>` returns `List.class`.
             */
            @JvmStatic
            protected fun getRawType(type: Type): Class<*> {
                return Utils.getRawType(type)
            }
        }
    }
}