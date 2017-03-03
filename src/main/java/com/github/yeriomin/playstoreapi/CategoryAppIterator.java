package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class CategoryAppIterator extends AppPageIterator {

    private String categoryId;
    private GooglePlayAPI.SUBCATEGORY subcategory;

    public CategoryAppIterator(GooglePlayAPI googlePlayApi, String categoryId, GooglePlayAPI.SUBCATEGORY subcategory) {
        super(googlePlayApi);
        this.categoryId = categoryId;
        this.subcategory = subcategory;
    }

    public String getCategoryId() {
        return categoryId;
    }

    @Override
    public ListResponse next() {
        String url = GooglePlayAPI.LIST_URL;
        Map<String, String> params = new HashMap<>();
        if (this.firstQuery) {
            params.put("cat", categoryId);
            params.put("ctr", subcategory.value);
        } else if (null != this.nextPageUrl && !this.nextPageUrl.isEmpty()) {
            url = this.nextPageUrl;
        } else {
            throw new NoSuchElementException();
        }

        ListResponse response;
        try {
            response = googlePlayApi.genericGet(url, params).getListResponse();
            this.firstQuery = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.nextPageUrl = getNextPageUrl(response);
        return response;
    }

    private String getNextPageUrl(ListResponse response) {
        if (null != response
            && response.getDocCount() > 0
            && response.getDoc(0).hasContainerMetadata()
            && response.getDoc(0).getContainerMetadata().hasNextPageUrl()
        ) {
            return GooglePlayAPI.FDFE_URL + response.getDoc(0).getContainerMetadata().getNextPageUrl();
        }
        return null;
    }
}
