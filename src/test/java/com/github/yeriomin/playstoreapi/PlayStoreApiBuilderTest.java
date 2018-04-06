package com.github.yeriomin.playstoreapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.fail;

public class PlayStoreApiBuilderTest {

    private MockOkHttpClientAdapter httpClientAdapter;
    private PropertiesDeviceInfoProvider deviceInfoProvider;
    private PlayStoreApiBuilder playStoreApiBuilder;

    @Before
    public void setUp() {
        httpClientAdapter = new MockOkHttpClientAdapter();
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-honami.properties"));
        } catch (IOException e) {
            fail("device-honami.properties not found");
        }
        Locale locale = Locale.ENGLISH;
        deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(locale.toString());
        deviceInfoProvider.setTimeToReport(1482626488L);
        playStoreApiBuilder = new PlayStoreApiBuilder().setLocale(locale);
    }

    @Test
    public void buildExceptionNoHttpClient() {
        try {
            playStoreApiBuilder.build();
            fail("ApiBuilderException was not thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ApiBuilderException);
            Assert.assertEquals("HttpClientAdapter is required", e.getMessage());
        }
    }

    @Test
    public void buildExceptionNoDeviceInfo() {
        playStoreApiBuilder.setHttpClient(httpClientAdapter);
        try {
            playStoreApiBuilder.build();
            fail("ApiBuilderException was not thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ApiBuilderException);
            Assert.assertEquals("DeviceInfoProvider is required", e.getMessage());
        }
    }

    @Test
    public void buildExceptionNoTokenNoPasswordNoTokenDispenser() {
        playStoreApiBuilder
            .setHttpClient(httpClientAdapter)
            .setDeviceInfoProvider(deviceInfoProvider)
        ;
        try {
            playStoreApiBuilder.build();
            fail("ApiBuilderException was not thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ApiBuilderException);
            Assert.assertEquals("Email-password pair, a token or a token dispenser url is required", e.getMessage());
        }
    }

    @Test
    public void buildExceptionPasswordNoTokenDispenserNoEmail() {
        playStoreApiBuilder
            .setHttpClient(httpClientAdapter)
            .setDeviceInfoProvider(deviceInfoProvider)
            .setPassword("a")
        ;
        try {
            playStoreApiBuilder.build();
            fail("ApiBuilderException was not thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ApiBuilderException);
            Assert.assertEquals("Email is required", e.getMessage());
        }
    }

    @Test
    public void buildEmailGsfIdPassword() throws Exception {
        playStoreApiBuilder
            .setLocale(Locale.US)
            .setHttpClient(httpClientAdapter)
            .setDeviceInfoProvider(deviceInfoProvider)
            .setEmail(GooglePlayAPITest.EMAIL)
            .setGsfId(GooglePlayAPITest.GSFID)
            .setPassword(GooglePlayAPITest.PASSWORD)
        ;
        GooglePlayAPI api = playStoreApiBuilder.buildUpon(new MockGooglePlayAPI());
        Assert.assertEquals("VgXBOjtk7TAASCefEdBRoow60YoyEYSqliUOaaiWkFKmWKZOUK-iXb1UgA184sTRpCVrKg.", api.getToken());
        Assert.assertEquals(1, httpClientAdapter.getRequests().size());
        Assert.assertEquals("/auth", httpClientAdapter.getRequests().get(0).url().encodedPath());
    }

    @Test
    public void buildGsfIdToken() throws Exception {
        playStoreApiBuilder
            .setLocale(Locale.US)
            .setHttpClient(httpClientAdapter)
            .setDeviceInfoProvider(deviceInfoProvider)
            .setGsfId(GooglePlayAPITest.GSFID)
            .setToken(GooglePlayAPITest.TOKEN)
        ;
        GooglePlayAPI api = playStoreApiBuilder.buildUpon(new MockGooglePlayAPI());
        Assert.assertEquals(GooglePlayAPITest.TOKEN, api.getToken());
        Assert.assertEquals(0, httpClientAdapter.getRequests().size());
    }

    @Test
    public void buildEmailPassword() throws Exception {
        playStoreApiBuilder
            .setLocale(Locale.US)
            .setHttpClient(httpClientAdapter)
            .setDeviceInfoProvider(deviceInfoProvider)
            .setEmail(GooglePlayAPITest.EMAIL)
            .setPassword(GooglePlayAPITest.PASSWORD)
        ;
        GooglePlayAPI api = playStoreApiBuilder.buildUpon(new MockGooglePlayAPI());
        Assert.assertEquals(5, httpClientAdapter.getRequests().size());
        Assert.assertEquals("/auth", httpClientAdapter.getRequests().get(0).url().encodedPath());
        Assert.assertEquals("/checkin", httpClientAdapter.getRequests().get(1).url().encodedPath());
        Assert.assertEquals("/checkin", httpClientAdapter.getRequests().get(2).url().encodedPath());
        Assert.assertEquals("/auth", httpClientAdapter.getRequests().get(3).url().encodedPath());
        Assert.assertEquals("/fdfe/uploadDeviceConfig", httpClientAdapter.getRequests().get(4).url().encodedPath());
        Assert.assertEquals("VgXBOjtk7TAASCefEdBRoow60YoyEYSqliUOaaiWkFKmWKZOUK-iXb1UgA184sTRpCVrKg.", api.getToken());
        Assert.assertEquals("307edaee584cc716", api.getGsfId());
    }
}