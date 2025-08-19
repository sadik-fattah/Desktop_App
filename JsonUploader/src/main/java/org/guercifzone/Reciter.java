package org.guercifzone;

import java.util.List;
import java.util.ArrayList;

public class Reciter {
    private String name;
    private String image;
    private String link;
    private String description;

    public Reciter() {}

    public Reciter(String name, String image, String link, String description) {
        this.name = name;
        this.image = image;
        this.link = link;
        this.description = description;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}