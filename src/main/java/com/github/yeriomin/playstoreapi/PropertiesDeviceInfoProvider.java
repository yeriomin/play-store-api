package com.github.yeriomin.playstoreapi;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesDeviceInfoProvider implements DeviceInfoProvider {

    static private String[] requiredFields = new String[] {
        "UserReadableName",
        "Build.HARDWARE",
        "Build.RADIO",
        "Build.BOOTLOADER",
        "Build.FINGERPRINT",
        "Build.BRAND",
        "Build.DEVICE",
        "Build.VERSION.SDK_INT",
        "Build.MODEL",
        "Build.MANUFACTURER",
        "Build.PRODUCT",
        "TouchScreen",
        "Keyboard",
        "Navigation",
        "ScreenLayout",
        "HasHardKeyboard",
        "HasFiveWayNavigation",
        "GL.Version",
        "GSF.version",
        "Vending.version",
        "Screen.Density",
        "Screen.Width",
        "Screen.Height",
        "Platforms",
        "SharedLibraries",
        "Features",
        "Locales",
        "CellOperator",
        "SimOperator",
        "Roaming",
        "Client",
        "TimeZone",
        "GL.Extensions"
    };

    private Properties properties;
    private String localeString;

    /**
     * Time to report to the google server
     * Introduced to make tests reproducible
     */
    private long timeToReport = System.currentTimeMillis() / 1000;

    public void setProperties(Properties properties) {
        ensureCompatibility(properties);
        this.properties = properties;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
    }

    void setTimeToReport(long timeToReport) {
        this.timeToReport = timeToReport;
    }

    public boolean isValid() {
        return properties.keySet().containsAll(Arrays.asList(requiredFields));
    }

    public int getSdkVersion() {
        return Integer.parseInt(this.properties.getProperty("Build.VERSION.SDK_INT"));
    }

    public int getPlayServicesVersion() {
        return Integer.parseInt(this.properties.getProperty("GSF.version"));
    }

    public String getMccmnc() {
        return properties.getProperty("SimOperator");
    }

    public String getAuthUserAgentString() {
        return "GoogleAuth/1.4 (" + properties.getProperty("Build.DEVICE") + " " + properties.getProperty("Build.ID") + ")";
    }

    public String getUserAgentString() {
        StringBuilder platforms = new StringBuilder();
        for (String platform: getList("Platforms")) {
            platforms.append(";").append(platform);
        }

        return "Android-Finsky/" + properties.getProperty("Vending.versionString") + " ("
            + "api=3"
            + ",versionCode=" + properties.getProperty("Vending.version")
            + ",sdk=" + properties.getProperty("Build.VERSION.SDK_INT")
            + ",device=" + properties.getProperty("Build.DEVICE")
            + ",hardware=" + properties.getProperty("Build.HARDWARE")
            + ",product=" + properties.getProperty("Build.PRODUCT")
            + ",platformVersionRelease=" + properties.getProperty("Build.VERSION.RELEASE")
            + ",model=" + properties.getProperty("Build.MODEL")
            + ",buildId=" + properties.getProperty("Build.ID")
            + ",isWideScreen=0"
            + ",supportedAbis=" + platforms.toString().substring(1)
            + ")"
        ;
    }

    public AndroidCheckinRequest generateAndroidCheckinRequest() {
        return AndroidCheckinRequest.newBuilder()
            .setId(0)
            .setCheckin(
                AndroidCheckinProto.newBuilder()
                    .setBuild(
                        AndroidBuildProto.newBuilder()
                            .setId(this.properties.getProperty("Build.FINGERPRINT"))
                            .setProduct(this.properties.getProperty("Build.HARDWARE"))
                            .setCarrier(this.properties.getProperty("Build.BRAND"))
                            .setRadio(this.properties.getProperty("Build.RADIO"))
                            .setBootloader(this.properties.getProperty("Build.BOOTLOADER"))
                            .setDevice(this.properties.getProperty("Build.DEVICE"))
                            .setSdkVersion(getInt("Build.VERSION.SDK_INT"))
                            .setModel(this.properties.getProperty("Build.MODEL"))
                            .setManufacturer(this.properties.getProperty("Build.MANUFACTURER"))
                            .setBuildProduct(this.properties.getProperty("Build.PRODUCT"))
                            .setClient(this.properties.getProperty("Client"))
                            .setOtaInstalled(Boolean.getBoolean(this.properties.getProperty("OtaInstalled")))
                            .setTimestamp(this.timeToReport)
                            .setGoogleServices(getInt("GSF.version"))
                    )
                    .setLastCheckinMsec(0)
                    .setCellOperator(this.properties.getProperty("CellOperator"))
                    .setSimOperator(this.properties.getProperty("SimOperator"))
                    .setRoaming(this.properties.getProperty("Roaming"))
                    .setUserNumber(0)
            )
            .setLocale(this.localeString)
            .setTimeZone(this.properties.getProperty("TimeZone"))
            .setVersion(3)
            .setDeviceConfiguration(getDeviceConfigurationProto())
            .setFragment(0)
            .build();
    }

    public DeviceConfigurationProto getDeviceConfigurationProto() {
        return DeviceConfigurationProto.newBuilder()
            .setTouchScreen(getInt("TouchScreen"))
            .setKeyboard(getInt("Keyboard"))
            .setNavigation(getInt("Navigation"))
            .setScreenLayout(getInt("ScreenLayout"))
            .setHasHardKeyboard(Boolean.getBoolean(this.properties.getProperty("HasHardKeyboard")))
            .setHasFiveWayNavigation(Boolean.getBoolean(this.properties.getProperty("HasFiveWayNavigation")))
            .setScreenDensity(getInt("Screen.Density"))
            .setScreenWidth(getInt("Screen.Width"))
            .setScreenHeight(getInt("Screen.Height"))
            .addAllNativePlatform(getList("Platforms"))
            .addAllSystemSharedLibrary(getList("SharedLibraries"))
            .addAllSystemAvailableFeature(getList("Features"))
            .addAllSystemSupportedLocale(getList("Locales"))
            .setGlEsVersion(getInt("GL.Version"))
            .addAllGlExtension(getList("GL.Extensions"))
            .build();
    }

    private int getInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private List<String> getList(String key) {
        return Arrays.asList(properties.getProperty(key).split(","));
    }

    private void ensureCompatibility(Properties properties) {
        if (!properties.containsKey("Vending.versionString") && properties.containsKey("Vending.version")) {
            String vendingVersionString = "7.1.15";
            if (properties.getProperty("Vending.version").length() > 6) {
                vendingVersionString = new StringBuilder(properties.getProperty("Vending.version").substring(2, 6)).insert(2, ".").insert(1, ".").toString();
            }
            properties.put("Vending.versionString", vendingVersionString);
        }
        if (properties.containsKey("Build.FINGERPRINT") && (!properties.containsKey("Build.ID") || !properties.containsKey("Build.VERSION.RELEASE"))) {
            String[] fingerprint = properties.getProperty("Build.FINGERPRINT").split("/");
            String buildId = "";
            String release = "";
            if (fingerprint.length > 5) {
                boolean releaseFound = false;
                for (String component: fingerprint) {
                    if (component.contains(":")) {
                        release = component.split(":")[1];
                        releaseFound = true;
                        continue;
                    }
                    if (releaseFound) {
                        buildId = component;
                        break;
                    }
                }
            }
            if (!properties.containsKey("Build.ID")) {
                properties.put("Build.ID", buildId);
            }
            if (!properties.containsKey("Build.VERSION.RELEASE")) {
                properties.put("Build.VERSION.RELEASE", release);
            }
        }
    }
}
