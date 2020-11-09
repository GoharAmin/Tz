package com.gohar_amin.tz.callback;

import java.util.List;

public interface ArrayCallback<T> extends BaseCallback{
    void onData(List<T> list);
}
