package osp.leobert.android.ssedemo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class OkhttpTestCase {

    public static String TAG = "OkHttpTestCase";

    public static void testOkhttpGet() {

        String url = "http://192.168.8.112:8080/sse/mvc/words";


        OkHttpClient okHttpClient = new OkHttpClient();
        EventSource.Factory factory = EventSources.createFactory(okHttpClient);

        Request.Builder builder = new Request.Builder().get().url(url);

        builder.addHeader("Content-Type", "text/event-stream")
                .addHeader("Accept-Encoding", "")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Cache-Control", "no-cache");

        builder.addHeader("Last-Event-ID", "2");

        Request request = builder.build();

        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onClosed(@NonNull EventSource eventSource) {
                super.onClosed(eventSource);
                Log.d(TAG, "on closed: ");
            }

            @Override
            public void onEvent(@NonNull EventSource eventSource, @Nullable String id, @Nullable String type, @NonNull String data) {
                super.onEvent(eventSource, id, type, data);
                Log.d(TAG, "on event: " + id + " " + type + " " + data);
            }

            @Override
            public void onFailure(@NonNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                super.onFailure(eventSource, t, response);
                Log.d(TAG, "on failure: ");
            }

            @Override
            public void onOpen(@NonNull EventSource eventSource, @NonNull Response response) {
                super.onOpen(eventSource, response);
                Log.d(TAG, "on open: ");
            }
        });

    }
}
