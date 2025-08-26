package org.Guercifzone.RemoteControlerPc;





import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.Guercifzone.Classes.ImageViewer;
import org.Guercifzone.FileApi.*;
import org.Guercifzone.MKC.MouseKeyboardControl;
import org.Guercifzone.Music.MusicPlayer;
import org.Guercifzone.Power.PowerOff;


public class Server {
    private JLabel messageLabel;

    public void connect(JButton resetButton, JLabel connectionStatusLabel, JLabel messageLabel, int port) {
        this.messageLabel = messageLabel;
        MouseKeyboardControl mouseControl = new MouseKeyboardControl();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();

        try {
            MainScreenController.clientSocket = MainScreenController.serverSocket.accept();
            SwingUtilities.invokeLater(() -> resetButton.setEnabled(false));
            InetAddress remoteInetAddress = MainScreenController.clientSocket.getInetAddress();
            String connectedMessage = "Connected to: " + remoteInetAddress;
            SwingUtilities.invokeLater(() -> connectionStatusLabel.setText(connectedMessage));
            this.showMessage(connectedMessage);
            (new ClientToAndroid()).connect(remoteInetAddress, port);
            MainScreenController.inputStream = MainScreenController.clientSocket.getInputStream();
            MainScreenController.outputStream = MainScreenController.clientSocket.getOutputStream();
            MainScreenController.objectOutputStream = new ObjectOutputStream(MainScreenController.outputStream);
            MainScreenController.objectInputStream = new ObjectInputStream(MainScreenController.inputStream);
            FileAPI fileAPI = new FileAPI();
            PowerOff powerOff = new PowerOff();
            MusicPlayer musicPlayer = new MusicPlayer();
            ImageViewer imageViewer = new ImageViewer();

            while(true) {
                try {
                    String message = (String)MainScreenController.objectInputStream.readObject();
                    if (message == null) {
                        SwingUtilities.invokeLater(() -> {
                            resetButton.setEnabled(true);
                            connectionStatusLabel.setText("Disconnected");
                        });
                        this.connectionClosed();
                        break;
                    }

                    switch (message) {
                        case "LEFT_CLICK":
                            mouseControl.leftClick();
                            break;
                        case "RIGHT_CLICK":
                            mouseControl.rightClick();
                            break;
                        case "DOUBLE_CLICK":
                            mouseControl.doubleClick();
                            break;
                        case "MOUSE_WHEEL":
                            int scrollAmount = (Integer)MainScreenController.objectInputStream.readObject();
                            mouseControl.mouseWheel(scrollAmount);
                            break;
                        case "MOUSE_MOVE":
                            int x = (Integer)MainScreenController.objectInputStream.readObject();
                            int y = (Integer)MainScreenController.objectInputStream.readObject();
                            Point point = MouseInfo.getPointerInfo().getLocation();
                            float nowx = (float)point.x;
                            float nowy = (float)point.y;
                            mouseControl.mouseMove((int)(nowx + (float)x), (int)(nowy + (float)y));
                            break;
                        case "MOUSE_MOVE_LIVE":
                            float xCord = (Float)MainScreenController.objectInputStream.readObject();
                            float yCord = (Float)MainScreenController.objectInputStream.readObject();
                            xCord *= (float)screenWidth;
                            yCord *= (float)screenHeight;
                            mouseControl.mouseMove((int)xCord, (int)yCord);
                            break;
                        case "KEY_PRESS":
                            int keyCode = (Integer)MainScreenController.objectInputStream.readObject();
                            mouseControl.keyPress(keyCode);
                            break;
                        case "KEY_RELEASE":
                            int keyCode1 = (Integer)MainScreenController.objectInputStream.readObject();
                            mouseControl.keyRelease(keyCode1);
                            break;
                        case "CTRL_ALT_T":
                            mouseControl.ctrlAltT();
                            break;
                        case "CTRL_SHIFT_Z":
                            mouseControl.ctrlShiftZ();
                            break;
                        case "ALT_F4":
                            mouseControl.altF4();
                            break;
                        case "TYPE_CHARACTER":
                            char ch = ((String)MainScreenController.objectInputStream.readObject()).charAt(0);
                            mouseControl.typeCharacter(ch);
                            break;
                        case "TYPE_KEY":
                            int typeKeyCode = (Integer)MainScreenController.objectInputStream.readObject();
                            mouseControl.typeCharacter(typeKeyCode);
                            break;
                        case "LEFT_ARROW_KEY":
                            mouseControl.pressLeftArrowKey();
                            break;
                        case "DOWN_ARROW_KEY":
                            mouseControl.pressDownArrowKey();
                            break;
                        case "RIGHT_ARROW_KEY":
                            mouseControl.pressRightArrowKey();
                            break;
                        case "UP_ARROW_KEY":
                            mouseControl.pressUpArrowKey();
                            break;
                        case "F5_KEY":
                            mouseControl.pressF5Key();
                            break;
                        case "FILE_DOWNLOAD_LIST_FILES":
                            String filePath = (String)MainScreenController.objectInputStream.readObject();
                            if (filePath.equals("/")) {
                                filePath = fileAPI.getHomeDirectoryPath();
                            }

                            (new SendFilesList()).sendFilesList(fileAPI, filePath, MainScreenController.objectOutputStream);
                            break;
                        case "FILE_DOWNLOAD_REQUEST":
                            String downloadFilePath = (String)MainScreenController.objectInputStream.readObject();
                            (new SendFile()).sendFile(downloadFilePath, MainScreenController.objectOutputStream);
                            break;
                        case "FILE_TRANSFER_REQUEST":
                            String fileName = (String)MainScreenController.objectInputStream.readObject();
                            long fileSize = (Long)MainScreenController.objectInputStream.readObject();
                            (new ReceiveFile()).receiveFile(fileName, fileSize, MainScreenController.objectInputStream);
                            break;
                        case "SHUTDOWN_PC":
                            powerOff.shutdown();
                            break;
                        case "RESTART_PC":
                            powerOff.restart();
                            break;
                        case "SLEEP_PC":
                            powerOff.suspend();
                            break;
                        case "LOCK_PC":
                            powerOff.lock();
                            break;
                        case "PLAY_MUSIC":
                            String musicFileName = (String)MainScreenController.objectInputStream.readObject();
                            String musicFilePath = (new FileAPI()).getHomeDirectoryPath();
                            musicFilePath = musicFilePath + "/RemoteControlPC/" + musicFileName;

                            try {
                                musicPlayer.playNewMedia(musicFilePath);
                                this.showMessage("Playing: " + musicFileName);
                            } catch (Exception var35) {
                                this.showMessage("Unsupported Media: " + musicFileName);
                            }
                            break;
                        case "SLIDE_MUSIC":
                            int slideDuration = (Integer)MainScreenController.objectInputStream.readObject();
                            musicPlayer.slide(slideDuration);
                            break;
                        case "PAUSE_OR_RESUME_MUSIC":
                            musicPlayer.resumeOrPauseMedia();
                            break;
                        case "STOP_MUSIC":
                            musicPlayer.stopMusic();
                            break;
                        case "SET_VOLUME_MUSIC":
                            float volume = (Float)MainScreenController.objectInputStream.readObject();
                            musicPlayer.setVolume((double)volume);
                            break;
                        case "SHOW_IMAGE":
                            String imageFileName = (String)MainScreenController.objectInputStream.readObject();
                            String imageFilePath = (new FileAPI()).getHomeDirectoryPath();
                            imageFilePath = imageFilePath + "/RemoteControlPC/" + imageFileName;
                            imageViewer.showImage(imageFileName, imageFilePath);
                            break;
                        case "CLOSE_IMAGE_VIEWER":
                            imageViewer.closeImageViewer();
                            break;
                        case "SCREENSHOT_REQUEST":
                            (new Screenshot()).sendScreenshot(MainScreenController.objectOutputStream);
                            break;
                        default:
                            System.out.println("Unknown message: " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.connectionClosed();
                    ClientToAndroid.closeConnectionToAndroid();
                    SwingUtilities.invokeLater(() -> {
                        resetButton.setEnabled(true);
                        connectionStatusLabel.setText("Disconnected");
                    });
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectionClosed() {
        try {
            if (MainScreenController.objectInputStream != null) {
                MainScreenController.objectInputStream.close();
            }
            if (MainScreenController.clientSocket != null) {
                MainScreenController.clientSocket.close();
            }
            if (MainScreenController.serverSocket != null) {
                MainScreenController.serverSocket.close();
            }
            if (MainScreenController.inputStream != null) {
                MainScreenController.inputStream.close();
            }
            if (MainScreenController.outputStream != null) {
                MainScreenController.outputStream.close();
            }
            if (MainScreenController.objectOutputStream != null) {
                MainScreenController.objectOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> this.messageLabel.setText(message));
    }
}