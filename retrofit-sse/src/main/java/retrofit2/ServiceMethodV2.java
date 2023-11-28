package retrofit2;

import static retrofit2.Utils.methodError;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Classname: ServiceMethodV2 </p>
 * Created by Leobert on 2023/11/27.
 */
abstract class ServiceMethodV2<T> extends ServiceMethod<T> {

    static <T> ServiceMethod<T> parseAnnotationsV2(RetrofitSSE retrofit, Method method) {
        RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit.retrofit, method);

        Type returnType = method.getGenericReturnType();
        if (Utils.hasUnresolvableType(returnType)) {
            throw methodError(
                    method,
                    "Method return type must not include a type variable or wildcard: %s",
                    returnType);
        }
        if (returnType == void.class) {
            throw methodError(method, "Service methods cannot return void.");
        }

        return HttpServiceMethodV2.parseAnnotations(retrofit, method, requestFactory);
    }
}
