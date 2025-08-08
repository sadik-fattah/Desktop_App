package org.guercifzone.ListVideo.Utils;


import java.util.ArrayList;
import java.util.List;

public class YoutubeUtils {
    public static List<String> extractVideoUrlsFromPlaylist(String playlistUrl) {
        // In a real implementation, you would parse the playlist and extract video URLs
        // This is just a simulation

        List<String> videoUrls = new ArrayList<>();

        // Simulate extracting 5 videos from playlist
        for (int i = 1; i <= 5; i++) {
            videoUrls.add("https://www.youtube.com/watch?v=video" + i);
        }

        return videoUrls;
    }

    public static String getVideoTitle(String videoUrl) {
        // In a real implementation, you would fetch the actual video title
        return "Video Title for " + videoUrl;
    }
}