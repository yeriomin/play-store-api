package com.github.yeriomin.playstoreapi;

import okhttp3.*;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GooglePlayAPITest {

    private static final String EMAIL = "username@gmail.com";
    private static final String PASSWORD = "password";
    private static final String GSFID = "3ebb78cfc63f8427";
    private static final String TOKEN = "HwSyrOumgrgi1MTCqHLY0KcpiGPvTfeOv1KpcGxzZ1V8nRSItH21IDchDvrDaRZpCstDYQ.";

    MockGooglePlayAPI api;

    @Before
    public void setUp() throws Exception {
        api = initApi();
    }

    @Test
    public void setLocale() throws Exception {
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
//        MockGooglePlayAPI api = initApi();
//        String gsfId = api.getGsfId();
//        System.out.println("gsfId " + gsfId);
//        api.setGsfId(gsfId);
//        String token = api.getToken();
//        System.out.println("token " + token);
//        api.setToken(token);
//        UploadDeviceConfigResponse response = api.uploadDeviceConfig();
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
        SearchResponse response = api.search("cpu");

        response.getDocList().get(0).getContainerMetadata().hasNextPageUrl();
        Assert.assertTrue(response.getDocCount() > 0);
        Assert.assertTrue(response.getDocList().get(0).hasContainerMetadata());
        Assert.assertTrue(response.getDocList().get(0).getContainerMetadata().hasNextPageUrl());
        Assert.assertEquals("clusterSearch?q=cpu&n=20&o=20&ecp=ggEFCgNjcHU%3D&ctntkn=-p6BnQMCCBQ%3D&fss=0&c=3&adsEnabled=1", response.getDocList().get(0).getContainerMetadata().getNextPageUrl());
        Assert.assertEquals(20, response.getDocList().get(0).getChildCount());
        DocV2 details = response.getDocList().get(0).getChild(0);
        Assert.assertEquals("CPU-Z", details.getTitle());

        List<Request> requests = api.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("search", request.url().pathSegments().get(1));
        Assert.assertEquals("3", request.url().queryParameter("c"));
        Assert.assertEquals("cpu", request.url().queryParameter("q"));
    }

    @Test
    public void getSearchIterator() throws Exception {
        GooglePlayAPI.SearchIterator i = api.getSearchIterator("cpu");
        Assert.assertEquals("cpu", i.getQuery());
        Assert.assertTrue(i.hasNext());

        SearchResponse response = i.next();
        Assert.assertTrue(response.getDocCount() > 0);
        Assert.assertTrue(response.getDocList().get(0).hasContainerMetadata());
        Assert.assertTrue(response.getDocList().get(0).getContainerMetadata().hasNextPageUrl());
        Assert.assertEquals("clusterSearch?q=cpu&n=20&o=20&ecp=ggEFCgNjcHU%3D&ctntkn=-p6BnQMCCBQ%3D&fss=0&c=3&adsEnabled=1", response.getDocList().get(0).getContainerMetadata().getNextPageUrl());
        Assert.assertEquals(20, response.getDocList().get(0).getChildCount());
        DocV2 details = response.getDocList().get(0).getChild(0);
        Assert.assertEquals("CPU-Z", details.getTitle());

        Assert.assertTrue(i.hasNext());
        SearchResponse response1 = i.next();
        Assert.assertTrue(response1.getDocCount() > 0);
        Assert.assertTrue(response1.getDocList().get(0).hasContainerMetadata());
        Assert.assertTrue(response1.getDocList().get(0).getContainerMetadata().hasNextPageUrl());
        Assert.assertEquals("clusterSearch?q=cpu&n=20&o=40&ecp=ggEFCgNjcHU%3D&ctntkn=-p6BnQMCCCg%3D&fss=0&c=3&adsEnabled=1", response1.getDocList().get(0).getContainerMetadata().getNextPageUrl());
        Assert.assertEquals(20, response1.getDocList().get(0).getChildCount());
        DocV2 details1 = response1.getDocList().get(0).getChild(0);
        Assert.assertEquals("AnTuTu Benchmark", details1.getTitle());
    }

    @Test
    public void details() throws Exception {
        DetailsResponse response = api.details("com.cpuid.cpu_z");

        DocV2 details = response.getDocV2();
        Assert.assertEquals("CPU-Z", details.getTitle());

        List<Request> requests = api.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("details", request.url().pathSegments().get(1));
        Assert.assertEquals("com.cpuid.cpu_z", request.url().queryParameter("doc"));
    }

    @Test
    public void bulkDetails() throws Exception {
        BulkDetailsResponse response = api.bulkDetails(Arrays.asList("com.cpuid.cpu_z", "org.torproject.android"));

        DocV2 details1 = response.getEntryList().get(0).getDoc();
        Assert.assertEquals("CPU-Z", details1.getTitle());
        DocV2 details2 = response.getEntryList().get(1).getDoc();
        Assert.assertEquals("Orbot: Proxy with Tor", details2.getTitle());

        List<Request> requests = api.getRequests();
        Assert.assertEquals(1, requests.size());
        Request request = requests.get(0);
        Assert.assertEquals(2, request.url().pathSegments().size());
        Assert.assertEquals("fdfe", request.url().pathSegments().get(0));
        Assert.assertEquals("bulkDetails", request.url().pathSegments().get(1));
        BulkDetailsRequest protoRequest = BulkDetailsRequest.parseFrom(getBodyBytes(request));
        Assert.assertEquals("com.cpuid.cpu_z", protoRequest.getDocid(0));
        Assert.assertEquals("org.torproject.android", protoRequest.getDocid(1));
    }

    @Test
    public void browse() throws Exception {
        BrowseResponse response = api.browse();
        BrowseLink link = response.getCategoryList().get(0);
        Assert.assertEquals("Android Wear", link.getName());
        Assert.assertEquals("homeV2?cat=ANDROID_WEAR&c=3", link.getDataUrl());
    }

    @Test
    public void list() throws Exception {
    }

    @Test
    public void purchase() throws Exception {
    }

    @Test
    public void reviews() throws Exception {
        ReviewResponse response = api.reviews("com.cpuid.cpu_z", GooglePlayAPI.REVIEW_SORT.HIGHRATING, 0, 20);
    }

    @Test
    public void recommendations() throws Exception {
        ListResponse response = api.recommendations("com.cpuid.cpu_z", GooglePlayAPI.RECOMMENDATION_TYPE.ALSO_INSTALLED, 0, 20);
    }

    private byte[] getBodyBytes(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readByteArray();
        } catch (final IOException e) {
            System.out.println("Could not read body");
        }
        return null;
    }

    private MockGooglePlayAPI initApi() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-honami.properties"));
        } catch (IOException e) {
            System.out.println("device-honami.properties not found");
            return null;
        }

        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());

        MockGooglePlayAPI api = new MockGooglePlayAPI(EMAIL, PASSWORD);
        api.setLocale(Locale.ENGLISH);
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setGsfId(GSFID);
        api.setToken(TOKEN);
        return api;
    }

}

class MockGooglePlayAPI extends GooglePlayAPI {

    ThrottledOkHttpClient getClient() {
        if (this.client == null) {
            this.client = new MockThrottledOkHttpClient();
        }
        return this.client;
    }

    public List<Request> getRequests() {
        return ((MockThrottledOkHttpClient) this.getClient()).getRequests();
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
        String fileName = getBodyFileName(request);
        System.out.println("Checking if " + fileName + " exists");
        URL url = getClass().getClassLoader().getResource(fileName);
        if (null != url) {
            String path = java.net.URLDecoder.decode(new File(url.getFile()).getAbsolutePath(), "UTF-8");
            System.out.println("Body FOUND. Reading from disk. " + path);
            body = read(path);
        } else {
            System.out.println("Body NOT found. Making a live request.");
            try {
                body = super.request(requestBuilder, headers);
            } catch (GooglePlayException e) {
                body = e.getBody();
            } finally {
                write(fileName, body);
            }
        }
        return body;
    }
	
	public List<Request> getRequests() {
		return this.requests;
	}

    private byte[] read(String path) {
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