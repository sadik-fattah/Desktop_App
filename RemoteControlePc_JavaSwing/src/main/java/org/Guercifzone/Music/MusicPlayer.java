package org.Guercifzone.Music;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author varun
 */
public class MusicPlayer {
    private Clip clip;
    private AudioInputStream audioInputStream;
    private boolean isPlaying = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }

    public void playNewMedia(String path) throws Exception {
        stopMusic(); // Stop any currently playing music

        try {
            File audioFile = new File(path);
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);

            clip.start();
            isPlaying = true;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new Exception("Could not play audio file: " + e.getMessage());
        }
    }

    public void resumeOrPauseMedia() {
        if (clip != null) {
            if (isPlaying) {
                clip.stop();
                isPlaying = false;
            } else {
                clip.start();
                isPlaying = true;
            }
        }
    }

    //in seconds, int
    public void slide(int seconds) {
        if (clip != null) {
            long microsecondPosition = seconds * 1000000L; // Convert seconds to microseconds
            if (microsecondPosition > clip.getMicrosecondLength()) {
                microsecondPosition = clip.getMicrosecondLength();
            }
            clip.setMicrosecondPosition(microsecondPosition);
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            isPlaying = false;
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                System.out.println("Error closing audio stream: " + e.getMessage());
            }
        }
    }

    public void setVolume(double value) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Convert from 0.0-1.0 scale to decibel scale (approx -80.0 to 6.0206)
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float dB = (float) (min + (max - min) * value);

            gainControl.setValue(dB);
        }
    }

    // Additional utility methods
    public long getDuration() {
        if (clip != null) {
            return clip.getMicrosecondLength() / 1000000L; // Return duration in seconds
        }
        return 0;
    }

    public long getCurrentPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition() / 1000000L; // Return current position in seconds
        }
        return 0;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}