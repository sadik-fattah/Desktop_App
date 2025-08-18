package org.guercifzone.Models;

public class AppInfo {
    private String appName;
    private String developerName;
    private String contactEmail;
    private String website;
    private String appDescription;

    public AppInfo() {
    }

    public AppInfo(String appName, String developerName, String contactEmail, String appDescription, String website) {
        this.appName = appName;
        this.developerName = developerName;
        this.contactEmail = contactEmail;
        this.appDescription = appDescription;
        this.website = website;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }
    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", developerName='" + developerName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", website='" + website + '\'' +
                ", appDescription='" + appDescription + '\'' +
                '}';
    }
}
