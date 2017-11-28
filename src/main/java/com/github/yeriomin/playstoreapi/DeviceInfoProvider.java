package com.github.yeriomin.playstoreapi;

public interface DeviceInfoProvider {

    AndroidCheckinRequest generateAndroidCheckinRequest();
    DeviceConfigurationProto getDeviceConfigurationProto();
    String getUserAgentString();
    String getAuthUserAgentString();
    int getSdkVersion();
    int getPlayServicesVersion();
    String getMccmnc();
}
