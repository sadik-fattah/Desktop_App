package org.guercifzone.Models;

public class Permissions {
    private boolean internetAccess;
    private boolean storageAccess;
    private boolean locationAccess;
    private boolean cameraAccess;
    private boolean microphoneAccess;
    private boolean contactsAccess;

    public Permissions() {
    }

    public Permissions(boolean internetAccess, boolean storageAccess, boolean locationAccess, boolean cameraAccess, boolean microphoneAccess, boolean contactsAccess) {
        this.internetAccess = internetAccess;
        this.storageAccess = storageAccess;
        this.locationAccess = locationAccess;
        this.cameraAccess = cameraAccess;
        this.microphoneAccess = microphoneAccess;
        this.contactsAccess = contactsAccess;
    }

    public boolean isInternetAccess() {
        return internetAccess;
    }

    public void setInternetAccess(boolean internetAccess) {
        this.internetAccess = internetAccess;
    }

    public boolean isStorageAccess() {
        return storageAccess;
    }

    public void setStorageAccess(boolean storageAccess) {
        this.storageAccess = storageAccess;
    }

    public boolean isLocationAccess() {
        return locationAccess;
    }

    public void setLocationAccess(boolean locationAccess) {
        this.locationAccess = locationAccess;
    }

    public boolean isCameraAccess() {
        return cameraAccess;
    }

    public void setCameraAccess(boolean cameraAccess) {
        this.cameraAccess = cameraAccess;
    }

    public boolean isMicrophoneAccess() {
        return microphoneAccess;
    }

    public void setMicrophoneAccess(boolean microphoneAccess) {
        this.microphoneAccess = microphoneAccess;
    }

    public boolean isContactsAccess() {
        return contactsAccess;
    }

    public void setContactsAccess(boolean contactsAccess) {
        this.contactsAccess = contactsAccess;
    }
    public boolean hasAnyPermission() {
        return internetAccess || storageAccess || locationAccess ||
                cameraAccess || microphoneAccess || contactsAccess;
    }

    @Override
    public String toString() {
        return "Permissions{" +
                "internetAccess=" + internetAccess +
                ", storageAccess=" + storageAccess +
                ", locationAccess=" + locationAccess +
                ", cameraAccess=" + cameraAccess +
                ", microphoneAccess=" + microphoneAccess +
                ", contactsAccess=" + contactsAccess +
                '}';
    }
}
