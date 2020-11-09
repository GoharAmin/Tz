package com.gohar_amin.tz.callback;

public interface PermissionCallback {
    void onAccess();
    void onDenied(String message);
}
