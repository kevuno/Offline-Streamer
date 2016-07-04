/**
 * Created by Kevin Bastian
 */

import javafx.application.Application;
        import javafx.geometry.Pos;
        import javafx.scene.Group;
        import javafx.scene.Scene;
        import javafx.scene.control.Label;
        import javafx.scene.control.ProgressBar;
        import javafx.scene.control.ProgressIndicator;
        import javafx.scene.layout.HBox;
        import javafx.stage.Stage;

public class loading  extends Application implements Runnable {

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 300, 75);
        stage.setScene(scene);
        stage.setTitle("Descarga de archivos");


        ProgressBar pb = new ProgressBar(-1.0);
        ProgressIndicator pi = new ProgressIndicator(-1.0);
        final Label label = new Label("Descargando archivos");

        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(label,pb,pi);
        scene.setRoot(hb);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void run() {
        launch();
    }
}