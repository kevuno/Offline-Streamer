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
import java.io.File;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

//Other imports
import java.net.URI;
import java.util.*;

import java.util.concurrent.Future;

/**
 *
 * @author dean
 */
public class player extends Application implements Runnable {


    private static VideoManager manager;

    /**
     * Main method that will just run the program
     */
    public void run() {
        launch();

    }




    @Override
    public void start(Stage primaryStage) {
        //Start the Web Socket

        //Create the video manager
        manager = new VideoManager();

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
        scene.setFill(Color.BLUE);
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

    LinkedList<String> queue = manager.getQueue();
        if (!queue.isEmpty()){
            System.out.println(queue);
            //Get the next item in the queue,save it into the player and push
            // it into the queue again
            String filename = queue.removeFirst();
            queue.add(filename);
            final File f = new File("videos/"+filename+".mp4");
            //Setting up the media to autoplay
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(f.toURI().toString()));
            mediaPlayer.setAutoPlay(true);
            //When the media ends, make a recursive call with the updated queue
            mediaPlayer.setOnEndOfMedia(() -> initMediaPlayer(mediaView));
            mediaView.setMediaPlayer(mediaPlayer);
        }
    }

    public static void setManager(String filenames){
        manager.setup(filenames);
    }


}