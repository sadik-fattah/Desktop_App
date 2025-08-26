package org.Guercifzone.Classes;

import static org.Guercifzone.RemoteControlerPc.MainScreenController.mainScreenController;

public class ImageViewer {

    public void showImage(final String name, final String path) {
        mainScreenController.showImage(name, path);
    }

    public void closeImageViewer() {
        mainScreenController.closeImageViewer();
    }
}