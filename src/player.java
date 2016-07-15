/*
 * Copyright (c) 2011, Pro JavaFX Authors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JFXtras nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *  FullScreenVideoPlayer.java - An example from the Pro JavaFX Platform book.
 */
//package projavafx.fullscreenvideoplayer;

//JavaFX imports
import java.awt.event.ActionEvent;
import java.io.File;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//Other imports
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.concurrent.Future;

/**
 *
 * @author dean
 */
public class Player extends Application implements Runnable {

    /**The Maganer object that will take care of the queue**/
    private static VideoManager manager;

    /**Counter**/
    private static int counter;

    /**
     * Main method that will just run the program
     */
    public void run(){launch(); counter=0;}

    @Override
    public void start(Stage primaryStage) {

        //Create the video manager
        manager = new VideoManager("lista.txt");

        try {
            manager.initManager();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("aasdasd");
        //Create the view and media with the queue in order to loop recursively
        MediaView mv = createMediaView();

        //Setup fullscreen mode
        final DoubleProperty width = mv.fitWidthProperty();
        final DoubleProperty height = mv.fitHeightProperty();
        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
        mv.setPreserveRatio(true);
        StackPane root = new StackPane();
        root.getChildren().add(mv);
        final Scene scene = new Scene(root, 1080, 1920);

        //Set up the window
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Full Screen Video Player");
        primaryStage.setFullScreen(true);
        primaryStage.show();

        //mv.getMediaPlayer().play();
    }

    public MediaView createMediaView(){
        MediaView mediaView = new MediaView();
        initMediaPlayer(mediaView);
        return mediaView;
    }

    private void initMediaPlayer(final MediaView mediaView) {
        System.out.println("Starting initMediaPlayer");
        counter++;

        if (!manager.isEmpty()) {

            //Print the queue for test
            System.out.println(manager.getQueue());


            //Get the next item from the manager and move the current item to the end
            String filename = manager.getNext();
            final File f = new File("videos/" + filename + ".mp4");
            //Setting up the media to autoplay
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(f.toURI().toString()));
            mediaPlayer.setAutoPlay(true);
            //When the media ends, make a recursive call with the updated queue
            mediaPlayer.setOnEndOfMedia(()->{
                mediaPlayer.dispose();
                System.out.println("END OF MEDIA");
                initMediaPlayer(mediaView);
            });
            System.out.println("Reproduction #"+counter);
            mediaView.setMediaPlayer(mediaPlayer);
        }else{
            System.out.println("empty");
            //There is no videos in the manager, try again in 10 seconds.
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initMediaPlayer(mediaView);
        }
    }

    public static void setManager(String codedMessage){
        try {
            manager.setupWs(codedMessage);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void alert() {

            /* block of code which need to execute via thread */
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Content here", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();

    }


}