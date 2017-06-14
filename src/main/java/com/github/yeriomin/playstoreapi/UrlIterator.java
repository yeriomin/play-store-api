package com.github.yeriomin.playstoreapi;

public class UrlIterator extends AppListIterator<ListResponse> {

    public UrlIterator(GooglePlayAPI googlePlayApi) {
        super(googlePlayApi);
    }

    public UrlIterator(GooglePlayAPI googlePlayApi, String firstPageUrl) {
        this(googlePlayApi);
        if (!firstPageUrl.startsWith(GooglePlayAPI.FDFE_URL)) {
            firstPageUrl = GooglePlayAPI.FDFE_URL + firstPageUrl;
        }
        this.firstPageUrl = firstPageUrl;
    }

    @Override
    protected ListResponse getAppListResponse(Payload payload) {
        return payload.getListResponse();
    }

    @Override
    protected String findNextPageUrl(ListResponse response) {
        if (null != response
            && response.getDocCount() > 0
            && response.getDoc(0).hasContainerMetadata()
            && response.getDoc(0).getContainerMetadata().hasNextPageUrl()
        ) {
            return GooglePlayAPI.FDFE_URL + response.getDoc(0).getContainerMetadata().getNextPageUrl();
        }
        return null;
    }

    @Override
    public ListResponse next() {
        return super.next();
    }
}
