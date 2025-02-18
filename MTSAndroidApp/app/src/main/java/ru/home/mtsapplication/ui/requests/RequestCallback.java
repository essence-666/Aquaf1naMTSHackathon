package ru.home.mtsapplication.ui.requests;

import ru.home.mtsapplication.models.RequestModel;

public interface RequestCallback {
    void onSuccess(RequestModel request);
    void onError(String error);
}

