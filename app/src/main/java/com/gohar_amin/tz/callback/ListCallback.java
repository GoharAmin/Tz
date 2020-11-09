package com.gohar_amin.tz.callback;

public interface ListCallback <T>{
    void onDelete(T t);
    void onEdit(T t);
}
