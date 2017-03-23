package com.github.yeriomin.playstoreapi;

import java.util.Iterator;

abstract public class AppListIterator implements Iterator {

    protected GooglePlayAPI googlePlayApi;
    protected boolean firstQuery = true;
    protected String nextPageUrl;

    public AppListIterator(GooglePlayAPI googlePlayApi) {
        this.googlePlayApi = googlePlayApi;
    }

    @Override
    public boolean hasNext() {
        return this.firstQuery || (null != this.nextPageUrl && this.nextPageUrl.length() > 0);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
