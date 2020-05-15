package com.example.tilitili.utils;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseHttpCallback<T> {

    Type type;

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class){
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    public BaseHttpCallback(){
        type = getSuperclassTypeParameter(getClass());
    }

    public abstract void onRequestBefore(Request request);
    public abstract void onFailure(Request request, IOException e);
    public abstract void onSuccess(Response response, T t);
    public abstract void onError(Response response, int code, Exception e);

}
