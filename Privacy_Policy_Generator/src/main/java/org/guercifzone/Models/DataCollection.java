package org.guercifzone.Models;

public class DataCollection {
    private boolean collectsEmail;
    private boolean collectsName;
    private boolean collectsPhone;
    private boolean collectsLocation;
    private boolean collectsUsageData;
    private boolean collectsDeviceInfo;
    private boolean usesCookies;
    private boolean usesThirdPartyServices;

    public DataCollection() {
        // Default constructor
    }

    public DataCollection(boolean collectsEmail, boolean collectsName, boolean collectsPhone,
                          boolean collectsLocation, boolean collectsUsageData,
                          boolean collectsDeviceInfo, boolean usesCookies,
                          boolean usesThirdPartyServices) {
        this.collectsEmail = collectsEmail;
        this.collectsName = collectsName;
        this.collectsPhone = collectsPhone;
        this.collectsLocation = collectsLocation;
        this.collectsUsageData = collectsUsageData;
        this.collectsDeviceInfo = collectsDeviceInfo;
        this.usesCookies = usesCookies;
        this.usesThirdPartyServices = usesThirdPartyServices;
    }

    // Getters and Setters
    public boolean collectsEmail() {
        return collectsEmail;
    }

    public void setCollectsEmail(boolean collectsEmail) {
        this.collectsEmail = collectsEmail;
    }

    public boolean collectsName() {
        return collectsName;
    }

    public void setCollectsName(boolean collectsName) {
        this.collectsName = collectsName;
    }

    public boolean collectsPhone() {
        return collectsPhone;
    }

    public void setCollectsPhone(boolean collectsPhone) {
        this.collectsPhone = collectsPhone;
    }

    public boolean collectsLocation() {
        return collectsLocation;
    }

    public void setCollectsLocation(boolean collectsLocation) {
        this.collectsLocation = collectsLocation;
    }

    public boolean collectsUsageData() {
        return collectsUsageData;
    }

    public void setCollectsUsageData(boolean collectsUsageData) {
        this.collectsUsageData = collectsUsageData;
    }

    public boolean collectsDeviceInfo() {
        return collectsDeviceInfo;
    }

    public void setCollectsDeviceInfo(boolean collectsDeviceInfo) {
        this.collectsDeviceInfo = collectsDeviceInfo;
    }

    public boolean usesCookies() {
        return usesCookies;
    }

    public void setUsesCookies(boolean usesCookies) {
        this.usesCookies = usesCookies;
    }

    public boolean usesThirdPartyServices() {
        return usesThirdPartyServices;
    }

    public void setUsesThirdPartyServices(boolean usesThirdPartyServices) {
        this.usesThirdPartyServices = usesThirdPartyServices;
    }

    public boolean collectsAnyData() {
        return collectsEmail || collectsName || collectsPhone || collectsLocation ||
                collectsUsageData || collectsDeviceInfo || usesCookies || usesThirdPartyServices;
    }

    @Override
    public String toString() {
        return "DataCollection{" +
                "collectsEmail=" + collectsEmail +
                ", collectsName=" + collectsName +
                ", collectsPhone=" + collectsPhone +
                ", collectsLocation=" + collectsLocation +
                ", collectsUsageData=" + collectsUsageData +
                ", collectsDeviceInfo=" + collectsDeviceInfo +
                ", usesCookies=" + usesCookies +
                ", usesThirdPartyServices=" + usesThirdPartyServices +
                '}';
    }
}
