package com.qre.services.networking;

public interface NetCallback<T> {

    void onSuccess(final T response);

    void onFailure(final Throwable exception);

}