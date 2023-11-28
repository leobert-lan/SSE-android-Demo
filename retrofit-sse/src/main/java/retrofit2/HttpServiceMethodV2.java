package retrofit2;

import static retrofit2.Utils.methodError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;

/**
 * Classname: HttpServiceMethodV2 </p>
 * Created by Leobert on 2023/11/27.
 */
abstract class HttpServiceMethodV2<ResponseT, ReturnT> extends HttpServiceMethod<ResponseT, ReturnT> {

    protected final RequestFactory requestFactory;

    HttpServiceMethodV2(RequestFactory requestFactory, Call.Factory callFactory, Converter<ResponseBody, ResponseT> responseConverter) {
        super(requestFactory, callFactory, responseConverter);
        this.requestFactory = requestFactory;
    }

    static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(
            RetrofitSSE retrofit, Method method, RequestFactory requestFactory) {

        //if sse
        Annotation[] annotations = method.getAnnotations();
        if (Utils.isAnnotationPresent(annotations, ServerSendEvents.class)) {
            Type adapterType = method.getGenericReturnType();

            EventSourceAdapter<ReturnT> eventSourceAdapter = createEventSourceAdapter(
                    retrofit, method, adapterType, annotations
            );

            return new EventSourceAdapted(
                    requestFactory,
                    retrofit.retrofit.callFactory(),
                    retrofit.retrofit.stringConverter(adapterType, annotations)/*just pass a nonnull param*/,
                    eventSourceAdapter,
                    retrofit.eventSourceFactory()
            );
        } else {
            return HttpServiceMethod.parseAnnotations(retrofit.retrofit, method, requestFactory);
        }

    }

    private static <ReturnT> EventSourceAdapter<ReturnT> createEventSourceAdapter(
            RetrofitSSE retrofitSSE, Method method, Type returnType, Annotation[] annotations) {
        try {
            //noinspection unchecked
            return (EventSourceAdapter<ReturnT>) retrofitSSE.eventSourceAdapter(returnType, annotations);
        } catch (RuntimeException e) { // Wide exception range because factories are user code.
            throw methodError(method, e, "Unable to create eventSource adapter for %s", returnType);
        }
    }

    static final class EventSourceAdapted<ResponseT, ReturnT> extends HttpServiceMethodV2<ResponseT, ReturnT> {

        private final EventSourceAdapter<ReturnT> eventSourceAdapter;
        private final EventSource.Factory eventSourceFactory;

        EventSourceAdapted(
                RequestFactory requestFactory,
                Call.Factory callFactory,
                Converter<ResponseBody, ResponseT> responseConverter,
                EventSourceAdapter<ReturnT> eventSourceAdapter,
                EventSource.Factory eventSourceFactory) {
            super(requestFactory, callFactory, responseConverter);
            this.eventSourceAdapter = eventSourceAdapter;
            this.eventSourceFactory = eventSourceFactory;
        }

        @Override
        protected ReturnT adapt(retrofit2.Call<ResponseT> call, Object[] args) {
            Request request;
            try {
                request = requestFactory.create(args);
                request = request.newBuilder().addHeader("Content-Type", "text/event-stream")
                        .addHeader("Accept-Encoding", "")
                        .addHeader("Accept", "text/event-stream")
                        .addHeader("Cache-Control", "no-cache")
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create request.", e);
            }
            return eventSourceAdapter.adapt(request, eventSourceFactory);
        }
    }
}
