/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.entities;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Y Sa
 */
public class DeviceInfoEnt {
    public class APP_NAME {

        public static final String APP_NDH = "AppNDH";
    }

    public class OS_NAME {

        public static final String IOS = "IOS";
        public static final String ANDROID = "ANDROID";
        public static final String WINDOWS_PHONE = "WINPHONE";;
    }
    @SerializedName("1")
    public String deviceId;
    @SerializedName("2")
    public String osName;
    @SerializedName("3")
    public String osVersion;
    @SerializedName("4")
    public String appName;
    @SerializedName("5")
    public String appVersion;
    @SerializedName("6")
    public String deviceName;
    @SerializedName("7")
    public String provider;
    @SerializedName("8")
    public String network;
    @SerializedName("9")
    public String language;
    @SerializedName("10")
    public String username;
    @SerializedName("11")
    public String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
