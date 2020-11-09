package com.gohar_amin.tz.callback;

public interface ConfirmationCallback<T> {
    void onConfirm(T t);
    void onCancel();
}
