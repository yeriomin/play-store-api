package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterates through search result pages
 * Each next() call gets you a next page of search results for the provided query
 */
public class SearchIterator extends AppListIterator {

    static private final String DOCID_FRAGMENT_MORE_RESULTS = "more_results";

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
        Map<String, String> params = new HashMap<String, String>();
        if (this.firstQuery) {
            params.put("c", "3");
            params.put("q", query);
        } else if (null != this.nextPageUrl && this.nextPageUrl.length() > 0) {
            url = this.nextPageUrl;
        } else {
            throw new NoSuchElementException();
        }

        SearchResponse response;
        try {
            response = googlePlayApi.genericGet(url, params).getSearchResponse();
            response = SearchResponse.newBuilder(response).setDoc(0, findApps(response.getDoc(0))).build();
            this.firstQuery = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        nextPageUrl = findNextPageUrl(response.getDoc(0));
        if (nextPageStartsFromZero()) {
            SearchResponse next = next();
            if (response.getDoc(0).getDocid().contains(DOCID_FRAGMENT_MORE_RESULTS)) {
                return addToStart(next, response.getDoc(0).getChild(0));
            }
            return next;
        }
        return response;
    }

    /**
     * Sometimes not a list of apps is returned by search, but a list of content types (music and apps, for example)
     * each of them having a list of items
     * In this case we have to find the apps list and return it
     */
    private DocV2 findApps(DocV2 doc) {
        if (doc.getChildCount() > 0 && doc.getChild(0).getBackendId() == 3 && doc.getChild(0).getDocType() == 1) {
            return doc;
        }
        if (doc.getChildCount() > 0 && doc.getChild(0).getChildCount() == 1) {
            DocV2 moreResults = findMoreResults(doc);
            if (null != moreResults) {
                return DocV2.newBuilder(moreResults).addChild(0, doc.getChild(0).getChild(0)).build();
            }
        }
        for (DocV2 child: doc.getChildList()) {
            DocV2 result = findApps(child);
            if (null != result) {
                return result;
            }
        }
        return null;
    }

    private DocV2 findMoreResults(DocV2 doc) {
        for (DocV2 child: doc.getChildList()) {
            if (child.getDocid().contains(DOCID_FRAGMENT_MORE_RESULTS)) {
                return child;
            }
        }
        return null;
    }

    private SearchResponse addToStart(SearchResponse old, DocV2 firstDoc) {
        DocV2 newDoc = DocV2.newBuilder(old.getDoc(0))
            .addChild(0, firstDoc)
            .build()
        ;
        return SearchResponse.newBuilder(old)
            .setDoc(0, newDoc)
            .build()
        ;
    }

    private String findNextPageUrl(DocV2 doc) {
        if (doc.hasContainerMetadata() && doc.getContainerMetadata().hasNextPageUrl()) {
            return GooglePlayAPI.FDFE_URL + doc.getContainerMetadata().getNextPageUrl();
        }
        if (doc.hasRelatedLinks()
            && doc.getRelatedLinks().hasUnknown1()
            && doc.getRelatedLinks().getUnknown1().hasUnknown2()
            && doc.getRelatedLinks().getUnknown1().getUnknown2().hasNextPageUrl()
        ) {
            return GooglePlayAPI.FDFE_URL + doc.getRelatedLinks().getUnknown1().getUnknown2().getNextPageUrl();
        }
        return null;
    }

    private boolean nextPageStartsFromZero() {
        if (null == nextPageUrl) {
            return false;
        }
        try {
            return new URI(nextPageUrl).getQuery().contains("o=0");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
