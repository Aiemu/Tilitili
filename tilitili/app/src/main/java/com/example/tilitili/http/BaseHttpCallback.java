package com.example.tilitili.http;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseHttpCallback<T> {

    protected Type type;

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    public BaseHttpCallback() {
        type = getSuperclassTypeParameter(getClass());
    }

    public abstract void onBeforeRequest(Request request);


    public abstract void onFailure(Request request, Exception e);

    /**
     * 请求成功时调用此方法
     *
     * @param response
     */
    public abstract void onResponse(Response response);

    /**
     * 状态码大于200，小于300 时调用此方法
     *
     * @param response
     * @param t
     * @throws IOException
     */
    public abstract void onSuccess(Response response, T t);

    /**
     * 状态码400，404，403，500等时调用此方法
     *
     * @param response
     * @param errorMessage
     * @param e
     */
    public abstract void onError(Response response, ErrorMessage errorMessage, Exception e);

    /**
     * Token 验证失败。状态码401,402,403 等时调用此方法
     *
     * @param response
     * @param code
     */
    public abstract void onTokenError(Response response, int code);

}
