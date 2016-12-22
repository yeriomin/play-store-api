package com.github.yeriomin.playstoreapi;

import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GooglePlayAPITest {

    private static final String EMAIL = "username@gmail.com";
    private static final String PASSWORD = "password";

    GooglePlayAPI api;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-honami.properties"));

        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());

        api = new MockGooglePlayAPI(EMAIL, PASSWORD);
        api.setLocale(Locale.getDefault());
        api.setDeviceInfoProvider(deviceInfoProvider);
//        String gsfId = api.getGsfId();
//        System.out.println("gsfId" + gsfId);
        String gsfId = "3ebb78cfc63f8426";
        api.setGsfId(gsfId);
//        String token = api.getToken();
//        System.out.println("token" + token);
        String token = "HwSyrOumgrgi1MTCqHLY0KcpiGPvTfeOv1KZcGxzZ1V8nRSItH21IDchDvrDaRZpCstDYQ.";
        api.setToken(token);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void setLocale() throws Exception {
        Assert.assertTrue(true);
    }

    @Test
    public void setDeviceInfoProvider() throws Exception {

    }

    @Test
    public void setToken() throws Exception {

    }

    @Test
    public void setGsfId() throws Exception {
    }

    @Test
    public void getGsfId() throws Exception {

    }

    @Test
    public void getToken() throws Exception {

    }

    @Test
    public void getAC2DMToken() throws Exception {

    }

    @Test
    public void c2dmRegister() throws Exception {

    }

    @Test
    public void search() throws Exception {
        api.search("cpu");
    }

    @Test
    public void getSearchIterator() throws Exception {
        api.getSearchIterator("cpu");
    }

    @Test
    public void details() throws Exception {
        api.details("com.cpuid.cpu_z");
    }

    @Test
    public void bulkDetails() throws Exception {
        api.bulkDetails(Arrays.asList("com.cpuid.cpu_z", "org.torproject.android"));
    }

    @Test
    public void browse() throws Exception {
        api.browse();
    }

    @Test
    public void list() throws Exception {
    }

    @Test
    public void purchase() throws Exception {
    }

    @Test
    public void reviews() throws Exception {
        api.reviews("com.cpuid.cpu_z", GooglePlayAPI.REVIEW_SORT.HIGHRATING, 0, 20);
    }

    @Test
    public void uploadDeviceConfig() throws Exception {
        api.uploadDeviceConfig();
    }

    @Test
    public void recommendations() throws Exception {
        api.recommendations("com.cpuid.cpu_z", GooglePlayAPI.RECOMMENDATION_TYPE.ALSO_INSTALLED, 0, 20);
    }

}

class MockGooglePlayAPI extends GooglePlayAPI {

    ThrottledOkHttpClient getClient() {
        if (this.client == null) {
            this.client = new MockThrottledOkHttpClient();
        }
        return this.client;
    }

    public MockGooglePlayAPI(String email, String password) {
        super(email, password);
    }
}

class MockThrottledOkHttpClient extends ThrottledOkHttpClient {
	
	private List<Request> requests = new ArrayList<>();

    @Override
    byte[] request(Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        byte[] body = null;
		Request request = requestBuilder.headers(Headers.of(headers)).build();
		this.requests.add(request);
        String path = getBodyFileName(request);
        System.out.println("Path to request body: " + path);
        if (Files.exists(Paths.get(path))) {
            System.out.println("Body FOUND. Reading from disk.");
            body = read(path);
        } else {
            System.out.println("Body NOT found. Making a live request.");
            try {
                body = super.request(requestBuilder, headers);
            } catch (GooglePlayException e) {
                body = e.getBody();
            } finally {
                write(path, body);
            }
        }
        return body;
    }
	
	public List<Request> getRequests() {
		return this.requests;
	}

    private static byte[] read(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Could not read from " + path + ": " + e.getMessage());
        }
        return null;
    }

    private static void write(String path, byte[] body) {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(path);
            stream.write(body);
            stream.close();
        } catch (IOException e) {
            System.out.println("Could not write to " + path + ": " + e.getMessage());
        }
    }

    private static String getBodyFileName(Request request) {
        StringBuilder query = new StringBuilder();
        for (String key: request.url().queryParameterNames()) {
            query.append("." + key + "." + request.url().queryParameter(key));
        }
        return "request" + request.url().encodedPath().replace("/", ".") + query + ".bin";
    }
}