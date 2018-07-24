package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 *
 * @author akdeniz, yeriomin
 */
public class GooglePlayAPI {

    public static final int AVAILABILITY_NOT_RESTRICTED = 1;
    public static final int AVAILABILITY_RESTRICTED_GEO = 2;
    public static final int AVAILABILITY_REMOVED = 7;
    public static final int AVAILABILITY_INCOMPATIBLE_DEVICE_APP = 9;

    public static final int IMAGE_TYPE_APP_SCREENSHOT = 1;
    public static final int IMAGE_TYPE_PLAY_STORE_PAGE_BACKGROUND = 2;
    public static final int IMAGE_TYPE_YOUTUBE_VIDEO_LINK = 3;
    public static final int IMAGE_TYPE_APP_ICON = 4;
    public static final int IMAGE_TYPE_CATEGORY_ICON = 5;
    public static final int IMAGE_TYPE_GOOGLE_PLUS_BACKGROUND = 15;

    private static final String SCHEME = "https://";
    private static final String HOST = "android.clients.google.com";
    private static final String CHECKIN_URL = SCHEME + HOST + "/checkin";
    private static final String URL_LOGIN = SCHEME + HOST + "/auth";
    private static final String C2DM_REGISTER_URL = SCHEME + HOST + "/c2dm/register2";
    public static final String FDFE_URL = SCHEME + HOST + "/fdfe/";
    public static final String LIST_URL = FDFE_URL + "list";
    private static final String ACCEPT_TOS_URL = FDFE_URL + "acceptTos";
    private static final String TOC_URL = FDFE_URL + "toc";
    private static final String BROWSE_URL = FDFE_URL + "browse";
    private static final String DETAILS_URL = FDFE_URL + "details";
    public static final String SEARCH_URL = FDFE_URL + "search";
    private static final String SEARCHSUGGEST_URL = FDFE_URL + "searchSuggest";
    private static final String BULKDETAILS_URL = FDFE_URL + "bulkDetails";
    private static final String PURCHASE_URL = FDFE_URL + "purchase";
    private static final String DELIVERY_URL = FDFE_URL + "delivery";
    private static final String REVIEWS_URL = FDFE_URL + "rev";
    private static final String ADD_REVIEW_URL = FDFE_URL + "addReview";
    private static final String DELETE_REVIEW_URL = FDFE_URL + "deleteReview";
    private static final String USER_REVIEW_URL = FDFE_URL + "userReview";
    private static final String ABUSE_URL = FDFE_URL + "flagContent";
    private static final String UPLOADDEVICECONFIG_URL = FDFE_URL + "uploadDeviceConfig";
    private static final String RECOMMENDATIONS_URL = FDFE_URL + "rec";
    private static final String CATEGORIES_URL = FDFE_URL + "categories";
    private static final String CATEGORIES_LIST_URL = FDFE_URL + "categoriesList";
    private static final String TESTING_PROGRAM_URL = FDFE_URL + "apps/testingProgram";
    private static final String LOG_URL = FDFE_URL + "log";
    private static final String LIBRARY_URL = FDFE_URL + "library";
    private static final String MODIFY_LIBRARY_URL = FDFE_URL + "modifyLibrary";
    private static final String API_FDFE_URL = FDFE_URL + "api/";
    private static final String USER_PROFILE_URL = API_FDFE_URL + "userProfile";

    private static final String ACCOUNT_TYPE_HOSTED_OR_GOOGLE = "HOSTED_OR_GOOGLE";

    public enum ABUSE {
        SEXUAL_CONTENT(1),
        GRAPHIC_VIOLENCE(3),
        HATEFUL_OR_ABUSIVE_CONTENT(4),
        IMPROPER_CONTENT_RATING(5),
        HARMFUL_TO_DEVICE_OR_DATA(7),
        OTHER(8),
        ILLEGAL_PRESCRIPTION(11),
        IMPERSONATION(12);

        public int value;

        ABUSE(int value) {
            this.value = value;
        }
    }

    public enum PATCH_FORMAT {
        GDIFF(1),
        GZIPPED_GDIFF(2),
        GZIPPED_BSDIFF(3),
        UNKNOWN_4(4),
        UNKNOWN_5(5);

        public int value;

        PATCH_FORMAT(int value) {
            this.value = value;
        }
    }

    public enum REVIEW_SORT {
        NEWEST(0), HIGHRATING(1), HELPFUL(4);

        public int value;

        REVIEW_SORT(int value) {
            this.value = value;
        }
    }

    public enum RECOMMENDATION_TYPE {
        ALSO_VIEWED(1), ALSO_INSTALLED(2);

        public int value;

        RECOMMENDATION_TYPE(int value) {
            this.value = value;
        }
    }

    public enum SEARCH_SUGGESTION_TYPE {
        SEARCH_STRING(2), APP(3);

        public int value;

        SEARCH_SUGGESTION_TYPE(int value) {
            this.value = value;
        }
    }

    public enum SUBCATEGORY {
        TOP_FREE("apps_topselling_free"), TOP_GROSSING("apps_topgrossing"), MOVERS_SHAKERS("apps_movers_shakers");

        public String value;

        SUBCATEGORY(String value) {
            this.value = value;
        }
    }

    public enum LIBRARY_ID {
        WISHLIST("u-wl");

        public String value;

        LIBRARY_ID(String value) {
            this.value = value;
        }
    }

    HttpClientAdapter client;
    private Locale locale;
    private DeviceInfoProvider deviceInfoProvider;

    /**
     * Auth token
     * It is a good idea to save and reuse it
     */
    private String token;

    /**
     * Google Services Framework id
     * Incorrectly called Android id and Device id sometimes
     * Is generated by a checkin request in generateGsfId()
     * It is a good idea to save and reuse it
     */
    private String gsfId;
    private String deviceCheckinConsistencyToken;
    private String deviceConfigToken;
    private String dfeCookie;

    public void setClient(HttpClientAdapter httpClient) {
        this.client = httpClient;
    }

    public HttpClientAdapter getClient() {
        return client;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setDeviceInfoProvider(DeviceInfoProvider deviceInfoProvider) {
        this.deviceInfoProvider = deviceInfoProvider;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setGsfId(String gsfId) {
        this.gsfId = gsfId;
    }

    public String getToken() {
        return token;
    }

    public String getGsfId() {
        return gsfId;
    }

    public String getDeviceCheckinConsistencyToken() {
        return deviceCheckinConsistencyToken;
    }

    public void setDeviceCheckinConsistencyToken(String deviceCheckinConsistencyToken) {
        this.deviceCheckinConsistencyToken = deviceCheckinConsistencyToken;
    }

    public String getDeviceConfigToken() {
        return deviceConfigToken;
    }

    public void setDeviceConfigToken(String deviceConfigToken) {
        this.deviceConfigToken = deviceConfigToken;
    }

    public String getDfeCookie() {
        return dfeCookie;
    }

    public void setDfeCookie(String dfeCookie) {
        this.dfeCookie = dfeCookie;
    }

    /**
     * Performs authentication on "ac2dm" service and match up gsf id,
     * security token and email by checking them in on this server.
     * <p>
     * This function sets check-inded gsf id and that can be taken either by
     * using <code>generateToken()</code> or from returned
     * {@link AndroidCheckinResponse} instance.
     */
    public String generateGsfId(String email, String ac2dmToken) throws IOException {
        // this first checkin is for generating gsf id
        AndroidCheckinRequest request = this.deviceInfoProvider.generateAndroidCheckinRequest();
        AndroidCheckinResponse checkinResponse1 = checkin(request.toByteArray());
        String securityToken = BigInteger.valueOf(checkinResponse1.getSecurityToken()).toString(16);

        // this is the second checkin to match credentials with gsf id
        AndroidCheckinRequest.Builder checkInbuilder = AndroidCheckinRequest.newBuilder(request);
        String gsfId = BigInteger.valueOf(checkinResponse1.getAndroidId()).toString(16);
        AndroidCheckinRequest build = checkInbuilder
                .setId(new BigInteger(gsfId, 16).longValue())
                .setSecurityToken(new BigInteger(securityToken, 16).longValue())
                .addAccountCookie("[" + email + "]")
                .addAccountCookie(ac2dmToken)
                .build();
        deviceCheckinConsistencyToken = checkin(build.toByteArray()).getDeviceCheckinConsistencyToken();

        return gsfId;
    }

    public TocResponse toc() throws IOException {
        byte[] responseBytes = client.get(TOC_URL, new HashMap<String, String>(), getDefaultHeaders());
        TocResponse tocResponse = ResponseWrapper.parseFrom(responseBytes).getPayload().getTocResponse();
        if (tocResponse.hasTosContent() && tocResponse.hasTosToken()) {
            acceptTos(tocResponse.getTosToken());
        }
        if (tocResponse.hasCookie()) {
            dfeCookie = tocResponse.getCookie();
        }
        return tocResponse;
    }

    private AcceptTosResponse acceptTos(String tosToken) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tost", tosToken);
        params.put("toscme", "false"); // Agree to receiving marketing emails or not
        byte[] responseBytes = client.post(ACCEPT_TOS_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getAcceptTosResponse();
    }

    /**
     * Posts given check-in request content and returns
     * {@link AndroidCheckinResponse}.
     */
    private AndroidCheckinResponse checkin(byte[] request) throws IOException {
        Map<String, String> headers = getDefaultHeaders();
        headers.put("Content-Type", "application/x-protobuffer");
        byte[] content = client.post(CHECKIN_URL, request, headers);
        return AndroidCheckinResponse.parseFrom(content);
    }

    /**
     * Authenticates on server with given email and password and sets
     * authentication token. This token can be used to login instead of using
     * email and password every time.
     */
    public String generateToken(String email, String password) throws IOException {
        Map<String, String> params = getDefaultLoginParams(email, password);
        params.put("service", "androidmarket");
        params.put("app", "com.android.vending");
        Map<String, String> headers = getAuthHeaders();
        headers.put("app", "com.android.vending");
        byte[] responseBytes = client.post(URL_LOGIN, params, headers);
        Map<String, String> response = parseResponse(new String(responseBytes));
        String secondRoundToken = null;
        if (response.containsKey("Token")) {
            secondRoundToken = generateTokenSecondRound(params, response.get("Token"));
        }
        if (null != secondRoundToken) {
            return secondRoundToken;
        } else if (response.containsKey("Auth")) {
            return response.get("Auth");
        } else {
            throw new AuthException("Authentication failed! (login)");
        }
    }

    /**
     * Since mid-october 2017 Auth-token from a single auth request has a time to live.
     * A second auth request with a secondary token needs to be made to get a token which lives longer.
     *
     */
    protected String generateTokenSecondRound(Map<String, String> previousParams, String secondaryToken) throws IOException {
        previousParams.put("Token", secondaryToken);
        if (this.gsfId != null && this.gsfId.length() > 0) {
            previousParams.put("androidId", this.gsfId);
        }
        previousParams.put("check_email", "1");
        previousParams.put("token_request_options", "CAA4AQ==");
        previousParams.put("system_partition", "1");
        previousParams.put("_opt_is_called_from_account_manager", "1");
        previousParams.remove("Email");
        previousParams.remove("EncryptedPasswd");
        Map<String, String> headers = getAuthHeaders();
        headers.put("app", "com.android.vending");
        byte[] responseBytes = client.post(URL_LOGIN, previousParams, headers);
        Map<String, String> response = parseResponse(new String(responseBytes));
        return response.containsKey("Auth") ? response.get("Auth") : null;
    }

    /**
     * Logins AC2DM server and returns authentication string.
     * This is used to link a device to an account.
     *
     */
    public String generateAC2DMToken(String email, String password) throws IOException {
        Map<String, String> params = getDefaultLoginParams(email, password);
        params.put("service", "ac2dm");
        params.put("add_account", "1");
        params.put("callerPkg", "com.google.android.gms");
        Map<String, String> headers = getAuthHeaders();
        headers.put("app", "com.google.android.gms");
        byte[] responseBytes = client.post(URL_LOGIN, params, headers);
        Map<String, String> response = parseResponse(new String(responseBytes));
        if (response.containsKey("Auth")) {
            return response.get("Auth");
        } else {
            throw new AuthException("Authentication failed! (loginAC2DM)");
        }
    }

    public Map<String, String> c2dmRegister(String application, String sender, String email, String password) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("app", application);
        params.put("sender", sender);
        params.put("device", new BigInteger(this.gsfId, 16).toString());
        Map<String, String> headers = getDefaultHeaders();
        headers.put("Authorization", "GoogleLogin auth=" + generateAC2DMToken(email, password));
        byte[] responseBytes = client.post(C2DM_REGISTER_URL, params, headers);
        return parseResponse(new String(responseBytes));
    }

    /**
     * A quick search which returns the most relevant app and a list of suggestions of current query continuation
     * In native Play Store this is used to fetch search suggestions as you type
     */
    public SearchSuggestResponse searchSuggest(String query) throws IOException {
        return searchSuggest(query, new SEARCH_SUGGESTION_TYPE[] { SEARCH_SUGGESTION_TYPE.APP, SEARCH_SUGGESTION_TYPE.SEARCH_STRING });
    }

    public SearchSuggestResponse searchSuggest(String query, SEARCH_SUGGESTION_TYPE[] types) throws IOException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("q", Collections.singletonList(query));
        params.put("c", Collections.singletonList("3"));
        params.put("ssis", Collections.singletonList("120"));
        List<String> typeStrings = new ArrayList<String>();
        for (SEARCH_SUGGESTION_TYPE type: types) {
            typeStrings.add(Integer.toString(type.value));
        }
        params.put("sst", typeStrings);

        byte[] responseBytes = client.getEx(SEARCHSUGGEST_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getSearchSuggestResponse();
    }

    /**
     * Fetches detailed information about passed package name.
     * If you need to fetch information about more than one application, consider using bulkDetails.
     */
    public DetailsResponse details(String packageName) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("doc", packageName);

        byte[] responseBytes = client.get(DETAILS_URL, params, getDefaultHeaders());
        ResponseWrapper w = ResponseWrapper.parseFrom(responseBytes);

        DetailsResponse detailsResponse = w.getPayload().getDetailsResponse();
        DetailsResponse.Builder detailsBuilder = DetailsResponse.newBuilder(detailsResponse);
        DocV2.Builder docV2Builder = DocV2.newBuilder(detailsResponse.getDocV2());
        for (PreFetch prefetch: w.getPreFetchList()) {
            Payload subPayload = prefetch.getResponse().getPayload();
            if (subPayload.hasListResponse()) {
                docV2Builder.addChild(subPayload.getListResponse().getDocList().get(0));
            }
            if (subPayload.hasReviewResponse()) {
                detailsBuilder.setUserReview(subPayload.getReviewResponse().getGetResponse().getReview(0));
            }
        }
        return detailsBuilder.setDocV2(docV2Builder).build();
    }

    /**
     * Fetches detailed information about each of the package names specified
     */
    public BulkDetailsResponse bulkDetails(List<String> packageNames) throws IOException {
        BulkDetailsRequest.Builder bulkDetailsRequestBuilder = BulkDetailsRequest.newBuilder();
        bulkDetailsRequestBuilder.addAllDocid(packageNames);
        byte[] request = bulkDetailsRequestBuilder.build().toByteArray();

        byte[] responseBytes = client.post(BULKDETAILS_URL, request, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getBulkDetailsResponse();
    }

    /**
     * Fetches available categories
     */
    public BrowseResponse browse() throws IOException {
        return browse(null, null);
    }

    public BrowseResponse browse(String categoryId, String subCategoryId) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "3");
        if (null != categoryId && categoryId.length() > 0) {
            params.put("cat", categoryId);
        }
        if (null != subCategoryId && subCategoryId.length() > 0) {
            params.put("ctr", subCategoryId);
        }
        byte[] responseBytes = client.get(BROWSE_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getBrowseResponse();
    }

    /**
     * This function is used for fetching download url and download cookie,
     * rather than actual purchasing.
     */
    public BuyResponse purchase(String packageName, int versionCode, int offerType) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ot", String.valueOf(offerType));
        params.put("doc", packageName);
        params.put("vc", String.valueOf(versionCode));
        byte[] responseBytes = client.post(PURCHASE_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getBuyResponse();
    }

    /**
     * Gets download links for an already purchased app. Can be used instead of purchase() for paid apps.
     * There is no point in using delivery() for free apps, because you still have to purchase() them
     * and purchase() returns the download info.
     *
     * @param packageName
     * @param versionCode
     * @param offerType
     */
    public DeliveryResponse delivery(String packageName, int versionCode, int offerType) throws IOException {
        return delivery(packageName, 0, versionCode, offerType, PATCH_FORMAT.GZIPPED_GDIFF, "");
    }

    public DeliveryResponse delivery(String packageName, int versionCode, int offerType, String downloadToken) throws IOException {
        return delivery(packageName, 0, versionCode, offerType, PATCH_FORMAT.GZIPPED_GDIFF, downloadToken);
    }

    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType, PATCH_FORMAT patchFormat) throws IOException {
        return delivery(packageName, installedVersionCode, updateVersionCode, offerType, patchFormat, "");
    }

    /**
     * Supplying a version code of an installed package and a patch format adds delta patch download
     * links to DeliveryResponse. Known patch formats are: 1,2,3,4,5
     *
     * @param packageName
     * @param installedVersionCode
     * @param updateVersionCode
     * @param offerType
     * @param patchFormat
     */
    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType, PATCH_FORMAT patchFormat, String downloadToken) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ot", String.valueOf(offerType));
        params.put("doc", packageName);
        params.put("vc", String.valueOf(updateVersionCode));
        if (installedVersionCode > 0) {
            params.put("bvc", String.valueOf(installedVersionCode));
            params.put("pf", String.valueOf(patchFormat.value));
        }
        if (null != downloadToken && downloadToken.length() > 0) {
            params.put("dtok", downloadToken);
        }
        byte[] responseBytes = client.get(DELIVERY_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getDeliveryResponse();
    }

    /**
     * Multiple patch formats are supported at the same time
     *
     * @param packageName
     * @param installedVersionCode
     * @param updateVersionCode
     * @param offerType
     * @return
     * @throws IOException
     */
    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType) throws IOException {
        return delivery(packageName, installedVersionCode, updateVersionCode, offerType, new PATCH_FORMAT[] {PATCH_FORMAT.GDIFF, PATCH_FORMAT.GZIPPED_GDIFF, PATCH_FORMAT.GZIPPED_BSDIFF}, "");
    }

    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType, String downloadToken) throws IOException {
        return delivery(packageName, installedVersionCode, updateVersionCode, offerType, new PATCH_FORMAT[] {PATCH_FORMAT.GDIFF, PATCH_FORMAT.GZIPPED_GDIFF, PATCH_FORMAT.GZIPPED_BSDIFF}, downloadToken);
    }

    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType, PATCH_FORMAT[] patchFormats) throws IOException {
        return delivery(packageName, installedVersionCode, updateVersionCode, offerType, patchFormats, "");
    }

    public DeliveryResponse delivery(String packageName, int installedVersionCode, int updateVersionCode, int offerType, PATCH_FORMAT[] patchFormats, String downloadToken) throws IOException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("ot", Collections.singletonList(String.valueOf(offerType)));
        params.put("doc", Collections.singletonList(packageName));
        params.put("vc", Collections.singletonList(String.valueOf(updateVersionCode)));
        if (installedVersionCode > 0) {
            params.put("bvc", Collections.singletonList(String.valueOf(installedVersionCode)));
            List<String> formatStrings = new ArrayList<String>();
            for (PATCH_FORMAT format: patchFormats) {
                formatStrings.add(Integer.toString(format.value));
            }
            params.put("pf", formatStrings);
        }
        if (null != downloadToken && downloadToken.length() > 0) {
            params.put("dtok", Collections.singletonList(downloadToken));
        }
        byte[] responseBytes = client.getEx(DELIVERY_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getDeliveryResponse();
    }

    /**
     * Fetches the reviews of given package name by sorting passed choice.
     *
     * Default values for offset and numberOfResults are "0" and "20" respectively.
     * If you request more than 20 reviews, you might get a malformed request exception.
     *
     * Supply version code to only get reviews for that version of the app
     */
    public ReviewResponse reviews(String packageName, REVIEW_SORT sort, Integer offset, Integer numberOfResults, Integer versionCode) throws IOException {
        // If you request more than 20 reviews, don't be surprised if you get a MalformedRequest exception
        Map<String, String> params = getPaginationParams(offset, numberOfResults);
        if (null != versionCode) {
            params.put("vc", String.valueOf(versionCode));
        }
        // "This device only" flag
        // Doesn't work properly even in Google Play Store
        // Also not implementing this because method signature gets fat
        // params.put("dfil", "1");
        params.put("doc", packageName);
        params.put("sort", (sort == null) ? null : String.valueOf(sort.value));
        byte[] responseBytes = client.get(REVIEWS_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getReviewResponse();
    }

    public ReviewResponse reviews(String packageName, REVIEW_SORT sort, Integer offset, Integer numberOfResults) throws IOException {
        return reviews(packageName, sort, offset, numberOfResults, null);
    }

    /**
     * Adds a review
     * Only package name and rating are mandatory
     *
     * @param packageName
     * @param comment
     * @param title
     * @param stars
     */
    public ReviewResponse addOrEditReview(String packageName, String comment, String title, int stars, boolean testing) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("doc", packageName);
        params.put("title", title);
        params.put("content", comment);
        params.put("rating", String.valueOf(stars));
        params.put("ipr", "true"); // I don't know what this does, but Google Play Store sends it
        params.put("itpr", testing ? "true" : "false"); // True for beta feedback, false for ordinary reviews
        byte[] responseBytes = client.postWithoutBody(ADD_REVIEW_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getReviewResponse();
    }

    public ReviewResponse addOrEditReview(String packageName, String comment, String title, int stars) throws IOException {
        return addOrEditReview(packageName, comment, title, stars, false);
    }

    /**
     * Returns the review which current user has left for given app
     *
     * @param packageName
     * @param testing
     * @return
     * @throws IOException
     */
    public ReviewResponse getReview(String packageName, boolean testing) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("doc", packageName);
        params.put("itpr", testing ? "true" : "false"); // True for beta feedback, false for ordinary reviews
        byte[] responseBytes = client.get(USER_REVIEW_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getReviewResponse();
    }

    public ReviewResponse getReview(String packageName) throws IOException {
        return getReview(packageName, false);
    }

    public ReviewResponse betaFeedback(String packageName, String comment) throws IOException {
        return addOrEditReview(packageName, comment, "", 5, true);
    }

    public void deleteReview(String packageName, boolean testing) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("doc", packageName);
        params.put("itpr", testing ? "true" : "false"); // True for beta feedback, false for ordinary reviews
        client.post(DELETE_REVIEW_URL, params, getDefaultHeaders());
    }

    public void deleteReview(String packageName) throws IOException {
        deleteReview(packageName, false);
    }

    public void deleteBetaFeedback(String packageName) throws IOException {
        deleteReview(packageName, true);
    }

    /**
     * If this is not done, some apps magically disappear from search responses
     */
    public UploadDeviceConfigResponse uploadDeviceConfig() throws IOException {
        UploadDeviceConfigRequest request = UploadDeviceConfigRequest.newBuilder()
                .setDeviceConfiguration(this.deviceInfoProvider.getDeviceConfigurationProto())
                .build();
        Map<String, String> headers = getDefaultHeaders();
        headers.put("X-DFE-Enabled-Experiments", "cl:billing.select_add_instrument_by_default");
        headers.put("X-DFE-Unsupported-Experiments", "nocache:billing.use_charging_poller,market_emails,buyer_currency,prod_baseline,checkin.set_asset_paid_app_field,shekel_test,content_ratings,buyer_currency_in_app,nocache:encrypted_apk,recent_changes");
        headers.put("X-DFE-SmallestScreenWidthDp", "320");
        headers.put("X-DFE-Filter-Level", "3");
        byte[] responseBytes = client.post(UPLOADDEVICECONFIG_URL, request.toByteArray(), headers);
        UploadDeviceConfigResponse response = ResponseWrapper.parseFrom(responseBytes).getPayload().getUploadDeviceConfigResponse();
        if (response.hasUploadDeviceConfigToken()) {
            deviceConfigToken = response.getUploadDeviceConfigToken();
        }
        return response;
    }

    /**
     * Fetches the recommendations of given package name.
     *
     * Default values for offset and numberOfResult are "0" and "20"
     * respectively. These values are determined by Google Play Store.
     */
    public ListResponse recommendations(String packageName, RECOMMENDATION_TYPE type, Integer offset, Integer numberOfResults) throws IOException {
        Map<String, String> params = getPaginationParams(offset, numberOfResults);
        params.put("doc", packageName);
        params.put("rt", (type == null) ? null : String.valueOf(type.value));
        byte[] responseBytes = client.get(RECOMMENDATIONS_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getListResponse();
    }

    /**
     * Fetches top level categories list
     */
    public BrowseResponse categories() throws IOException {
        return categories(null);
    }

    /**
     * This seems to be the default since 04.2017
     */
    public ListResponse categoriesList() throws IOException {
        return categoriesList(null);
    }

    /**
     * Fetches sub categories of the given category
     */
    public BrowseResponse categories(String category) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "3");
        if (null != category && category.length() > 0) {
            params.put("cat", category);
        }
        byte[] responseBytes = client.get(CATEGORIES_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getBrowseResponse();
    }

    /**
     * This seems to be the default since 04.2017
     */
    public ListResponse categoriesList(String category) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "3");
        if (null != category && category.length() > 0) {
            params.put("cat", category);
        }
        byte[] responseBytes = client.get(CATEGORIES_LIST_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getListResponse();
    }

    /**
     * Use this with the urls which play store returns: next page urls, suggests and so on
     *
     */
    public Payload genericGet(String url, Map<String, String> params) throws IOException {
        if (null == params) {
            params = new HashMap<String, String>();
        }
        byte[] responseBytes = client.get(url, params, getDefaultHeaders());
        ResponseWrapper wrapper = ResponseWrapper.parseFrom(responseBytes);
        Payload payload = wrapper.getPayload();
        if (wrapper.getPreFetchCount() > 0
            && ((payload.hasSearchResponse() && payload.getSearchResponse().getDocCount() == 0)
                || (payload.hasListResponse() && payload.getListResponse().getDocCount() == 0)
                || payload.hasBrowseResponse()
            )
        ) {
            return wrapper.getPreFetch(0).getResponse().getPayload();
        }
        return payload;
    }

    /**
     * Subscribe to or unsubscribe from the testing program of given app
     *
     * @param packageName
     * @param subscribe Set this to false to unsubscribe
     */
    public TestingProgramResponse testingProgram(String packageName, boolean subscribe) throws IOException {
        TestingProgramRequest request = TestingProgramRequest.newBuilder()
            .setPackageName(packageName)
            .setSubscribe(subscribe)
            .build()
        ;
        byte[] responseBytes = client.post(TESTING_PROGRAM_URL, request.toByteArray(), getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getTestingProgramResponse();
    }

    /**
     * Put the given app into current user's app library.
     * Without this download links are not returned with purchase/delivery responses.
     *
     * @param packageName
     * @param timestamp
     */
    public String log(String packageName, long timestamp) throws IOException {
        LogRequest request = LogRequest.newBuilder()
            .setDownloadConfirmationQuery("confirmFreeDownload?doc=" + packageName)
            .setTimestamp(timestamp)
            .build()
        ;
        byte[] responseBytes = client.post(LOG_URL, request.toByteArray(), getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getLogResponse();
    }

    public String log(String packageName) throws IOException {
        return log(packageName, System.currentTimeMillis());
    }

    public boolean reportAbuse(String packageName, ABUSE reason, String content) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("doc", packageName);
        params.put("cft", String.valueOf(reason.value));
        if ((reason == ABUSE.OTHER || reason == ABUSE.HARMFUL_TO_DEVICE_OR_DATA) && null != content && content.length() > 0) {
            params.put("content", content);
        }
        return ResponseWrapper.parseFrom(client.post(ABUSE_URL, params, getDefaultHeaders())).getPayload().hasFlagContentResponse();
    }

    public UserProfileResponse userProfile() throws IOException {
        return ResponseWrapperApi.parseFrom(
                client.get(USER_PROFILE_URL, new HashMap<String, String>(), getDefaultHeaders())
        ).getPayload().getUserProfileResponse();
    }

    public void addLibraryApp(LIBRARY_ID libraryId, String packageName) throws IOException {
        ModifyLibraryRequest request = ModifyLibraryRequest.newBuilder()
            .addAddPackageName(packageName)
            .setLibraryId(libraryId.value)
            .build()
        ;
        client.post(MODIFY_LIBRARY_URL, request.toByteArray(), getDefaultHeaders());
    }

    public void addWishlistApp(String packageName) throws IOException {
        addLibraryApp(LIBRARY_ID.WISHLIST, packageName);
    }

    public void removeLibraryApp(LIBRARY_ID libraryId, String packageName) throws IOException {
        ModifyLibraryRequest request = ModifyLibraryRequest.newBuilder()
            .addRemovePackageName(packageName)
            .setLibraryId(libraryId.value)
            .build()
        ;
        client.post(MODIFY_LIBRARY_URL, request.toByteArray(), getDefaultHeaders());
    }

    public void removeWishlistApp(String packageName) throws IOException {
        removeLibraryApp(LIBRARY_ID.WISHLIST, packageName);
    }

    public ListResponse getLibraryApps(LIBRARY_ID libraryId) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "0");
        params.put("dl", "7"); // magic
        if (null != libraryId) {
            params.put("libid", libraryId.value);
        }
        byte[] responseBytes = client.get(LIBRARY_URL, params, getDefaultHeaders());
        return ResponseWrapper.parseFrom(responseBytes).getPayload().getListResponse();
    }

    public ListResponse getWishlistApps() throws IOException {
        return getLibraryApps(LIBRARY_ID.WISHLIST);
    }

    /**
     * login methods use this
     * Most likely not all of these are required, but the Market app sends them, so we will too
     *
     * client_sig is SHA1 digest of encoded certificate on
     * GoogleLoginService(package name : com.google.android.gsf) system APK.
     * But google doesn't seem to care of value of this parameter.
     */
    protected Map<String, String> getDefaultLoginParams(String email, String password) throws GooglePlayException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Email", email);
        try {
            params.put("EncryptedPasswd", PasswordEncrypter.encrypt(email, password));
        } catch (GeneralSecurityException e1) {
            throw new GooglePlayException("Could not encrypt password", e1);
        } catch (UnsupportedEncodingException e2) {
            throw new GooglePlayException("Could not encrypt password", e2);
        }
        params.put("accountType", ACCOUNT_TYPE_HOSTED_OR_GOOGLE);
        params.put("google_play_services_version", String.valueOf(this.deviceInfoProvider.getPlayServicesVersion()));
        params.put("has_permission", "1");
        params.put("source", "android");
        params.put("device_country", this.locale.getCountry().toLowerCase());
        params.put("lang", this.locale.getLanguage().toLowerCase());
        params.put("sdk_version", String.valueOf(this.deviceInfoProvider.getSdkVersion()));
        params.put("client_sig", "38918a453d07199354f8b19af05ec6562ced5788");
        params.put("callerSig", "38918a453d07199354f8b19af05ec6562ced5788");
        return params;
    }

    /**
     * Using Accept-Language you can fetch localized information such as reviews and descriptions.
     * Note that changing this value has no affect on localized application list that
     * server provides. It depends on only your IP location.
     *
     */
    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        if (this.token != null && this.token.length() > 0) {
            headers.put("Authorization", "GoogleLogin auth=" + this.token);
        }
        headers.put("User-Agent", this.deviceInfoProvider.getUserAgentString());
        if (this.gsfId != null && this.gsfId.length() > 0) {
            headers.put("X-DFE-Device-Id", this.gsfId);
        }
        headers.put("Accept-Language", this.locale.toString().replace("_", "-"));
        // This is a base64-encoded EncodedTargets protobuf message
        // toc request returns available targets in the Experiments field,
        // but supported targets are AFAIK static for each build of the play store app
        // Which means this list is going to remain static
        headers.put("X-DFE-Encoded-Targets", "CAESpwJX6pSBBqYK0QJCAtgDAQEUkgeAAqQItQFYQJkBuwQykgHpCpgBugEyhgEvaPIC3QEn3AEW+wu4AwECzwWuA5oTNdEIvAHbELYBAaUDngMBLyjjC8MCowKtA7AC9AOvDbgC0wHfBlcBqgKbAssBUYMDF272AeUBTIgCGALlAQIUswEHYkJLYgHXCg2hBNwBQE4BYRP6AS1dMvMCogKAA80CtgGrBMgB3gQKwQGHAZMCYgnaAmwPiAJjMQEizQLmAYYCvgEB3QEOE7kBqgHEA9cCHAelAQHFAToBA/MBiQGOAQEH5QGWBANGAQYHCOsBygFXyQHlAQUcMbsCZ5sBlAKQAjjfAgElbI4KkwVwRYIBggc1kwE5KtAB1gN6jwU2RckBsQScAtENGqQHEQEBAQEBAskBHCvOAe0BAgMEawMEAS+A088CgruxAwEBAgMECQgJAQIIBAECAQYBAQQFBAgNBgIMAwMDAQ0BAQEFAQEBxgEBEgQEAg0mwQF9LwIcAQEKkAEMMxcBIQoUDwYHIjeEAQ4MFk0JWH8RERgBA4sBgQEUECMIEXBkEQ9fC6MBwAKEAQSIAYoBGRgLKxYWBQcBKmUCAiUocxQnLfQEMQ43GIUBjQG0AVlCjgEeMiQQJi9b1AFiA3cJAQowrgF5qgEMAyxkngEEgQF0UEXUAYoBzAIFBQnNAQQBRaABCDE4pwJgNS7OAQ1yqwEgiwM/+wImlwMeQ60ChAZ24wWCBAkE9gMWc5wBVW0BCTwB3gUgEA57VV6VAYYGKxjYAQEhAQcCIAgSHQemAzgBGkaEAQG7AnV3MBgBIgKjAhIBARgWD8YLHYABhwGEAsoBAQIBwwEn6wIBOQHbAVLnA0H1AsIBdQETKQSLAbIDSpsBBhI/RDgUK1VFU48CgwIKDgcvXBdSGrkBDvcBtwEqFAHSA98DlwEE6wGHAWIu0wEGExILWigkAQIChAW1AQ0GI1konwEyHhgBBCACVgEjApABHRIbJ36JAV0MD/0BIyYiBAEiKh6AAj8EGwMXIIoBUj2yAcoCCxixAiV+G1q7AQyIASV3iwGBAUcBKwU3AlQBYqQCITABDUUDngMdsQFxfxBmvQQL7AEHOIwBHgyNAwFxAQIVoAFragI6UQgCCYoEFBQCAwExMlMYAgPKAZkBOgEBBleEATumAgosyQEWWzZHiQEZOCYOXjIRNJ8BP0ZGvwIEKCZhERw/iQEcJVMGV5EBMgEKngLSAgQSTSUCjAGDARF1IDKQAgzKAQICAgcEAQQCBgQDBgUHBAIGBgQCBAIGBQICAgYEAwQe0wF+VTkhJB8oNgEBCCkBaTt0BAIEAQYEAwSyAbACJoQCBgcGBhUCKx0SBAoBbQYGAwICBjgIPg0JOGkbCJEBdw4NAz0uhAEGARGEAQ0hCAJE1wE8IcIBAYcBQQEJXR4eBgMWGitnKywePhcDRAgKjQEUPEsNXjk6BQcFBgcEAQYEAwVADiUEAQcGBgQfDIYBsgMpBTsCBQIKWSYHGv0BBQMJLg5YAiEJCk45FgIjCBUMIRoCJARXnAFCNwcEAQYGMFcbKnm5AhAJHgMKLy4ZBQMCAQIDAkMj1AEIqQMFBREJNheoAQurAQECJCGDARIyARFDBgYGBAM");
        headers.put("X-DFE-Client-Id", "am-android-google");
        headers.put("X-DFE-Network-Type", "4");
        headers.put("X-DFE-Content-Filters", "");
        headers.put("X-DFE-UserLanguages", this.locale.toString());
        headers.put("X-DFE-Request-Params", "timeoutMs=30000");
        if (null != deviceCheckinConsistencyToken && deviceCheckinConsistencyToken.length() > 0) {
            headers.put("X-DFE-Device-Checkin-Consistency-Token", deviceCheckinConsistencyToken);
        }
        if (null != deviceConfigToken && deviceConfigToken.length() > 0) {
            headers.put("X-DFE-Device-Config-Token", deviceConfigToken);
        }
        if (null != dfeCookie && dfeCookie.length() > 0) {
            headers.put("X-DFE-Cookie", dfeCookie);
        }
        String mccmnc = deviceInfoProvider.getMccmnc();
        if (null != mccmnc && mccmnc.length() > 0) {
            headers.put("X-DFE-MCCMNC", mccmnc);
        }
        return headers;
    }

    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.deviceInfoProvider.getAuthUserAgentString());
        if (this.gsfId != null && this.gsfId.length() > 0) {
            headers.put("device", this.gsfId);
        }
        return headers;
    }

    /**
     * Offset/limit params
     *
     * @param offset
     * @param numberOfResults
     */
    private Map<String, String> getPaginationParams(Integer offset, Integer numberOfResults) {
        Map<String, String> params = new HashMap<String, String>();
        if (offset != null) {
            params.put("o", String.valueOf(offset));
        }
        if (numberOfResults != null) {
            params.put("n", String.valueOf(numberOfResults));
        }
        return params;
    }

    /**
     * Some methods instead of a protobuf return key-value pairs on each string
     *
     * @param response
     */
    public static Map<String, String> parseResponse(String response) {
        Map<String, String> keyValueMap = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(response, "\n\r");
        while (st.hasMoreTokens()) {
            String[] keyValue = st.nextToken().split("=", 2);
            if (keyValue.length >= 2) {
                keyValueMap.put(keyValue[0], keyValue[1]);
            }
        }
        return keyValueMap;
    }
}
