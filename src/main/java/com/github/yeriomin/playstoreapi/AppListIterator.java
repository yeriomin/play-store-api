package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract public class AppListIterator<T> implements Iterator<T> {

    protected GooglePlayAPI googlePlayApi;
    protected boolean firstQuery = true;
    protected String firstPageUrl;
    protected String nextPageUrl;

    abstract protected T getAppListResponse(Payload payload);
    abstract protected String findNextPageUrl(T response);

    public AppListIterator(GooglePlayAPI googlePlayApi) {
        this.googlePlayApi = googlePlayApi;
    }

    public T next() {
        T response;
        try {
            response = getAppListResponse(getPayload());
            this.firstQuery = false;
        } catch (IOException e) {
            throw new IteratorGooglePlayException(e);
        }
        nextPageUrl = findNextPageUrl(response);
        return response;
    }

    public boolean hasNext() {
        return this.firstQuery || (null != this.nextPageUrl && this.nextPageUrl.length() > 0);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected Payload getPayload() throws IOException {
        String url;
        if (firstQuery && null != firstPageUrl) {
            url = firstPageUrl;
        } else if (null != nextPageUrl && nextPageUrl.length() > 0) {
            url = nextPageUrl;
        } else {
            throw new NoSuchElementException();
        }
        return googlePlayApi.genericGet(url, null);
    }
}
