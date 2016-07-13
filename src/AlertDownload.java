/**
 * Created by Kevin Bastian
 */

import javafx.application.Application;
        import javafx.geometry.Pos;
        import javafx.scene.Group;
        import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import javax.swing.*;

public class AlertDownload implements Runnable {

    private JDialog dialog;
    @Override
    public void run() {
        String alertMessage = "No se encontro ningun archivo de video, descargando...\n";
        JFrame frame = new JFrame();
        JDialog dialog = new JDialog(frame,alertMessage);

        dialog.setVisible(true); // to visible the dialog
        this.dialog = dialog;
    }

    public void hideAlert(){
        this.dialog.setVisible(false);
    }
}