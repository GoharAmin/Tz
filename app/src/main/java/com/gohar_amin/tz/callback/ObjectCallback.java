package com.gohar_amin.tz.callback;

public interface ObjectCallback<T> extends BaseCallback {
    void onData(T t);
}
