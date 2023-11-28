package retrofit2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.sse.EventSource;

/**
 * Classname: RetrofitSSE </p>
 * Created by Leobert on 2023/11/27.
 */
public final class RetrofitSSE {
    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();

    final Retrofit retrofit;

    @NotNull
    final EventSource.Factory eventSourceFactory;

    final List<EventSourceAdapter.Factory> eventSourceAdapterFactories = new ArrayList<>();

    public RetrofitSSE(Retrofit retrofit, @NotNull EventSource.Factory eventSourceFactory) {
        this.retrofit = retrofit;
        this.eventSourceFactory = eventSourceFactory;
    }


    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> service) {
        validateServiceInterface(service);
        return (T)
                Proxy.newProxyInstance(
                        service.getClassLoader(),
                        new Class<?>[]{service},
                        new InvocationHandler() {
                            private final Platform platform = Platform.get();
                            private final Object[] emptyArgs = new Object[0];

                            @Override
                            public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                                    throws Throwable {
                                // If the method is a method from Object then defer to normal invocation.
                                if (method.getDeclaringClass() == Object.class) {
                                    return method.invoke(this, args);
                                }
                                args = args != null ? args : emptyArgs;
                                return platform.isDefaultMethod(method)
                                        ? platform.invokeDefaultMethod(method, service, proxy, args)
                                        : loadServiceMethod(method).invoke(args);
                            }
                        });
    }

    public RetrofitSSE addEventSourceAdapterFactory(EventSourceAdapter.Factory factory) {
        eventSourceAdapterFactories.add(Objects.requireNonNull(factory, "factory == null"));
        return this;
    }

    private void validateServiceInterface(Class<?> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }

        Deque<Class<?>> check = new ArrayDeque<>(1);
        check.add(service);
        while (!check.isEmpty()) {
            Class<?> candidate = check.removeFirst();
            if (candidate.getTypeParameters().length != 0) {
                StringBuilder message =
                        new StringBuilder("Type parameters are unsupported on ").append(candidate.getName());
                if (candidate != service) {
                    message.append(" which is an interface of ").append(service.getName());
                }
                throw new IllegalArgumentException(message.toString());
            }
            Collections.addAll(check, candidate.getInterfaces());
        }

        if (retrofit.validateEagerly) {
            Platform platform = Platform.get();
            for (Method method : service.getDeclaredMethods()) {
                if (!platform.isDefaultMethod(method) && !Modifier.isStatic(method.getModifiers())) {
                    loadServiceMethod(method);
                }
            }
        }
    }

    ServiceMethod<?> loadServiceMethod(Method method) {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = ServiceMethodV2.parseAnnotationsV2(this, method);
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public EventSource.Factory eventSourceFactory() {
        return eventSourceFactory;
    }

    public EventSourceAdapter<?> eventSourceAdapter(Type returnType, Annotation[] annotations) {
        return nextEventSourceAdapter(null, returnType, annotations);
    }


    public EventSourceAdapter<?> nextEventSourceAdapter(
            @Nullable EventSourceAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {
        Objects.requireNonNull(returnType, "returnType == null");
        Objects.requireNonNull(annotations, "annotations == null");

        int start = eventSourceAdapterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = eventSourceAdapterFactories.size(); i < count; i++) {
            EventSourceAdapter<?> adapter = eventSourceAdapterFactories.get(i).get(returnType, annotations, this);
            if (adapter != null) {
                return adapter;
            }
        }

        StringBuilder builder =
                new StringBuilder("Could not locate eventSource adapter for ").append(returnType).append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(eventSourceAdapterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = eventSourceAdapterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(eventSourceAdapterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }


}

