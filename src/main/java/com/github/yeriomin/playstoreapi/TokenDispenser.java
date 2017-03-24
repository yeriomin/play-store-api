package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TokenDispenser {

    static public final String DISPENSER_URL = "http://tokendispenser-yeriomin.rhcloud.com";

    static private final String RESOURCE_TOKEN = "token";
    static private final String RESOURCE_TOKEN_AC2DM = "token-ac2dm";

    static private final String PARAMETER_EMAIL = "email";

    static public String getToken(HttpClientAdapter httpClient, String email) throws IOException {
        return request(httpClient, getUrl(RESOURCE_TOKEN, email));
    }

    static public String getTokenAc2dm(HttpClientAdapter httpClient, String email) throws IOException {
        return request(httpClient, getUrl(RESOURCE_TOKEN_AC2DM, email));
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
