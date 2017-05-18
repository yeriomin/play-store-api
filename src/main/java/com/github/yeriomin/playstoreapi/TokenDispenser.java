package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class TokenDispenser {

    static public final String DISPENSER_URL = "http://tokendispenser-yeriomin.rhcloud.com";

    static private final String RESOURCE_TOKEN = "token";
    static private final String RESOURCE_TOKEN_AC2DM = "token-ac2dm";

    static private final String PARAMETER_EMAIL = "email";

    static private final int RETRY_COUNT = 3;
    static private final int RETRY_INTERVAL = 5000;

    static public String getToken(HttpClientAdapter httpClient, String email) throws IOException {
        return requestAndRetry(httpClient, getUrl(RESOURCE_TOKEN, email));
    }

    static public String getTokenAc2dm(HttpClientAdapter httpClient, String email) throws IOException {
        return requestAndRetry(httpClient, getUrl(RESOURCE_TOKEN_AC2DM, email));
    }

    static private String requestAndRetry(HttpClientAdapter httpClient, String url) throws IOException {
        GooglePlayException ae = new AuthException("Token dispenser failed to auth. Rate exceeded?");
        int retries = RETRY_COUNT;
        while (retries > 0) {
            retries--;
            System.out.println("Attempt #" + (RETRY_COUNT - retries));
            try {
                return request(httpClient, url);
            } catch (AuthException e) {
                ae = e;
                if (retries > 0) {
                    sleep();
                } else {
                    break;
                }
            }
        }
        throw ae;
    }

    static private void sleep() {
        try {
            Thread.sleep(RETRY_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    static private String getUrl(String resource, String email) {
        try {
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Unlikely
        }
        return DISPENSER_URL + "/" + resource + "/" + PARAMETER_EMAIL + "/" + email;
    }

    static private String request(HttpClientAdapter httpClient, String url) throws IOException {
        System.out.println("Requesting " + url);
        try {
            return new String(httpClient.get(url));
        } catch (GooglePlayException e) {
            if (e.getCode() == 404) {
                throw new TokenDispenserException("Token dispenser has no password for " + url);
            } else {
                throw e;
            }
        }
    }
}
