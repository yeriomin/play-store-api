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

    public GooglePlayAPI build() throws IOException, ApiBuilderException {
        GooglePlayAPI api = new GooglePlayAPI();
        api.setLocale(null == locale ? Locale.getDefault() : locale);
        api.setClient(httpClient);
        if (null == deviceInfoProvider) {
            throw new ApiBuilderException("DeviceInfoProvider is required");
        } else {
            api.setDeviceInfoProvider(deviceInfoProvider);
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
        return api;
    }

    private String generateGsfId(final GooglePlayAPI api) throws IOException, ApiBuilderException {
        if (isEmpty(email)) {
            throw new ApiBuilderException("Email is required, unless you provide both token and gsfId");
        }
        String tokenAc2dm = new AuthRepeater(httpClient) {
            @Override
            protected String request() throws IOException {
                return isEmpty(password)
                    ? TokenDispenser.getTokenAc2dm(api.getClient(), email)
                    : api.generateAC2DMToken(email, password)
                ;
            }
        }.getToken();
        return api.generateGsfId(email, tokenAc2dm);
    }

    private String generateToken(final GooglePlayAPI api) throws IOException, ApiBuilderException {
        if (isEmpty(email)) {
            throw new ApiBuilderException("Email is required, unless you provide both token and gsfId");
        }
        return new AuthRepeater(httpClient) {
            @Override
            protected String request() throws IOException {
                return isEmpty(password)
                    ? TokenDispenser.getToken(api.getClient(), email)
                    : api.generateToken(email, password)
                ;
            }
        }.getToken();
    }

    private boolean isEmpty(String value) {
        return null == value || value.length() == 0;
    }
}
