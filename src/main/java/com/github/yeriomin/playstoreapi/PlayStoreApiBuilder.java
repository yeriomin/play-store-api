package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.util.Locale;

public class PlayStoreApiBuilder {

    private String email;
    private String password;
    private String gsfId;
    private String token;
    private Locale locale;
    private DeviceInfoProvider deviceInfoProvider;
    private HttpClientAdapter httpClient;
    private String tokenDispenserUrl;
    private TokenDispenserClient tokenDispenserClient;
    private String deviceCheckinConsistencyToken;
    private String deviceConfigToken;
    private String dfeCookie;

    public PlayStoreApiBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public PlayStoreApiBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public PlayStoreApiBuilder setGsfId(String gsfId) {
        this.gsfId = gsfId;
        return this;
    }

    public PlayStoreApiBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    public PlayStoreApiBuilder setDeviceCheckinConsistencyToken(String deviceCheckinConsistencyToken) {
        this.deviceCheckinConsistencyToken = deviceCheckinConsistencyToken;
        return this;
    }

    public PlayStoreApiBuilder setDeviceConfigToken(String deviceConfigToken) {
        this.deviceConfigToken = deviceConfigToken;
        return this;
    }

    public PlayStoreApiBuilder setDfeCookie(String dfeCookie) {
        this.dfeCookie = dfeCookie;
        return this;
    }

    public PlayStoreApiBuilder setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public PlayStoreApiBuilder setDeviceInfoProvider(DeviceInfoProvider deviceInfoProvider) {
        this.deviceInfoProvider = deviceInfoProvider;
        return this;
    }

    public PlayStoreApiBuilder setHttpClient(HttpClientAdapter httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PlayStoreApiBuilder setTokenDispenserUrl(String tokenDispenserUrl) {
        this.tokenDispenserUrl = tokenDispenserUrl;
        return this;
    }

    public GooglePlayAPI build() throws IOException, ApiBuilderException {
        return buildUpon(new GooglePlayAPI());
    }

    public GooglePlayAPI buildUpon(GooglePlayAPI api) throws IOException, ApiBuilderException {
        api.setLocale(null == locale ? Locale.getDefault() : locale);
        api.setClient(httpClient);
        if (null == httpClient) {
            throw new ApiBuilderException("HttpClientAdapter is required");
        } else {
            api.setClient(httpClient);
        }
        if (null == deviceInfoProvider) {
            throw new ApiBuilderException("DeviceInfoProvider is required");
        } else {
            api.setDeviceInfoProvider(deviceInfoProvider);
        }
        if (isEmpty(password) && isEmpty(token) && isEmpty(tokenDispenserUrl)) {
            throw new ApiBuilderException("Email-password pair, a token or a token dispenser url is required");
        }
        if (!isEmpty(tokenDispenserUrl)) {
            tokenDispenserClient = new TokenDispenserClient(tokenDispenserUrl, httpClient);
        }
        if ((isEmpty(token) || isEmpty(gsfId)) && isEmpty(email) && null != tokenDispenserClient) {
            email = tokenDispenserClient.getRandomEmail();
            if (isEmpty(email)) {
                throw new ApiBuilderException("Could not get email from token dispenser");
            }
        }
        if (isEmpty(email) && (isEmpty(token) || isEmpty(gsfId))) {
            throw new ApiBuilderException("Email is required");
        }
        boolean needToUploadDeviceConfig = false;
        if (isEmpty(gsfId)) {
            gsfId = generateGsfId(api);
            needToUploadDeviceConfig = true;
        }
        api.setGsfId(gsfId);
        if (isEmpty(token)) {
            token = generateToken(api);
        }
        api.setToken(token);
        if (needToUploadDeviceConfig) {
            api.uploadDeviceConfig();
        }
        if (isEmpty(api.getDeviceCheckinConsistencyToken())) {
            api.setDeviceCheckinConsistencyToken(deviceCheckinConsistencyToken);
        }
        if (isEmpty(api.getDeviceConfigToken())) {
            api.setDeviceConfigToken(deviceConfigToken);
        }
        if (isEmpty(api.getDfeCookie())) {
            api.setDfeCookie(dfeCookie);
        }
        return api;
    }

    private String generateGsfId(GooglePlayAPI api) throws IOException, ApiBuilderException {
        String tokenAc2dm = isEmpty(password) ? tokenDispenserClient.getTokenAc2dm(email) : api.generateAC2DMToken(email, password);
        return api.generateGsfId(email, tokenAc2dm);
    }

    private String generateToken(GooglePlayAPI api) throws IOException, ApiBuilderException {
        return isEmpty(password) ? tokenDispenserClient.getToken(email) : api.generateToken(email, password);
    }

    private boolean isEmpty(String value) {
        return null == value || value.length() == 0;
    }
}
