package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * Iterates through search result pages
 * Each next() call gets you a next page of search results for the provided query
 */
public class SearchIterator extends AppListIterator {

    private final String query;

    public SearchIterator(GooglePlayAPI googlePlayApi, String query) {
        super(googlePlayApi);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public SearchResponse next() {
        String url = GooglePlayAPI.SEARCH_URL;
        Map<String, String> params = new HashMap<>();
        if (this.firstQuery) {
            params.put("c", "3");
            params.put("q", query);
        } else if (null != this.nextPageUrl && !this.nextPageUrl.isEmpty()) {
            url = this.nextPageUrl;
        } else {
            throw new NoSuchElementException();
        }

        SearchResponse response;
        try {
            response = googlePlayApi.genericGet(url, params).getSearchResponse();
            if (notOnlyApps(response)) {
                String abnormalNextPageUrl = getAbnormalNextPageUrl(response);
                if (null != abnormalNextPageUrl) {
                    response = googlePlayApi.genericGet(abnormalNextPageUrl, params).getSearchResponse();
                }
            }
            this.firstQuery = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.nextPageUrl = getNextPageUrl(response);
        return response;
    }

    private String getNextPageUrl(SearchResponse response) {
        if (null == response || response.getDocCount() == 0) {
            return null;
        }
        DocV2 doc = response.getDoc(0);
        if (doc.hasContainerMetadata() && doc.getContainerMetadata().hasNextPageUrl()) {
            return GooglePlayAPI.FDFE_URL + doc.getContainerMetadata().getNextPageUrl();
        }
        return null;
    }

    /**
     * Sometimes not a list of apps is returned by search, but a list of content types (music and apps, for example)
     * each of them having a list of items
     * In this case we have to find the apps list and return it
     */
    private boolean notOnlyApps(SearchResponse response) {
        return response.getDocList().get(0).getDocType() != 45 || !response.getDocList().get(0).getTitle().equals("Apps");
    }

    private String getAbnormalNextPageUrl(SearchResponse response) {
        if (null == response || response.getDocCount() == 0) {
            return null;
        }
        DocV2 doc = response.getDoc(0);
        for (DocV2 child: doc.getChildList()) {
            if (child.getDocType() == 45
                && child.getTitle().equals("Apps")
                && child.hasRelatedLinks()
                && child.getRelatedLinks().hasUnknown1()
                && child.getRelatedLinks().getUnknown1().hasUnknown2()
                && child.getRelatedLinks().getUnknown1().getUnknown2().hasNextPageUrl()
            ) {
                return GooglePlayAPI.FDFE_URL + child.getRelatedLinks().getUnknown1().getUnknown2().getNextPageUrl();
            }
        }
        return null;
    }
}
