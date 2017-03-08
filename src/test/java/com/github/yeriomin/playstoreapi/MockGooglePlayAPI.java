package com.github.yeriomin.playstoreapi;

import java.util.List;

import okhttp3.Request;

class MockGooglePlayAPI extends GooglePlayAPI {

    public OkHttpClientWrapper getClient() {
        if (this.client == null) {
            this.client = new MockOkHttpClientWrapper();
        }
        return this.client;
    }

    List<Request> getRequests() {
        return ((MockOkHttpClientWrapper) this.getClient()).getRequests();
    }
}
