import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kevin
 */
public class VideoManager {

    /**The queue of the program, it has a pair of values a name and the original position of the queue**/
    private LinkedList<Pair> queue;

    /**The filename of the list for the queue**/
    private String listFilename;

    public VideoManager(String listFilename){
        this.listFilename = listFilename;
        this.queue = new LinkedList<>();
    }


    /**
     * First time running the program, it will be set up using this method
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setup() throws FileNotFoundException, UnsupportedEncodingException {
        //Read from the list filename and check

        File file =  new File("videos/"+listFilename);
        //Check if the list file exists in the videos directory, if not it will be created
        if(!file.exists()){
            System.out.println("Falta archivo de lista de videos, creando uno nuevo...");

            PrintWriter writerFIle = new PrintWriter("videos/"+listFilename, "UTF-8");

            writerFIle.close();

        }else{
            //Open list file and read to insert into queue
            File listFile = new File("videos/"+listFilename);
            Scanner input = new Scanner(listFile);

            //This queue that will contain the names from the list file that exist in the videos dir.
            LinkedList<Pair> existQueue = new LinkedList<>();
            //This queue will only contain the names from the list file that don't exist in the videos dir.
            LinkedList<Pair> nonExistQueue = new LinkedList<>();

            /*Go through the names in the file and add to the corresponding queues */

            Integer position_count = 0;
            while (input.hasNextLine()){
                //Names
                String vidName = input.nextLine().trim();
                String completeVidName = vidName+".mp4";

                //Create node to insert in queue
                Pair node = new Pair(vidName,position_count);

                //Only if it is not found in the directory, add to non exist queue.
                if(!new File("videos/"+completeVidName).exists()){
                    nonExistQueue.add(node);
                }else{
                    //Add to the exist queue
                    existQueue.add(node);
                }
                position_count++;
            }
            input.close();

            //Call the download method
            this.queue = Download(nonExistQueue,existQueue);

        }
    }

    /**
     * If the exist Queue, which contains the filenames that are found in the directory, is empty
     * it means that there was not a single video found, so the program will play an alert
     * start downloading. It will download the first item in the queue, once it finishes, it will remove
     * the alert, play the VideoPlayer and start downloading in the background the missing files.
     * In case there is at least one file in the directory, from the once that are required, then it will already
     * play the VideoPlayer and download if nesessary
     * @param noExistQueue: The queue that contains the files that are missing
     * @param existQueue: The queue that contains the files that were found
     */

    public LinkedList<Pair> Download(LinkedList<Pair> noExistQueue, LinkedList<Pair> existQueue){

        LinkedList<Pair> finalQueue = new LinkedList<>();

        if(noExistQueue.isEmpty()){
            return existQueue;
        }

        if(existQueue.isEmpty()){

            //Alert and download only first file
            alert();


            //Get first item out of the queue:
            Pair first_node = noExistQueue.removeFirst();
            FileDownloader downloader = new FileDownloader(first_node.getname());
            if(downloader.downloadFromFile()){
                finalQueue.add(first_node);
            }

            //TODO: quit the alert
        }


        Thread downloads = new Thread(new FileDownloader(noExistQueue));

        downloads.start();

        System.out.println("STARTING PLAYER");
        //TODO: get notification from downloader that a node was downloaded so that it can be added to the queue

        //TODO: see why is file not available even though it finished the download
        // using addNode method



        //Start the missing files downloads.


        //Return only the queue with the files that existed + were downloaded

        return noExistQueue;
    }



    /**
     * Adds the given node to the queue according to the position given by the node
     * @param node: The node to be added to the queue
     */
    public void addNode(Pair node){
        //Append to the beggining if queue is empty
        if(queue.isEmpty()){
           queue.addFirst(node);

        }else{

            //If the first node of the queue is bigger than the value of the node to be inserte
            //then we append the node at the front of the queue
            if(queue.get(0).getvalue() > node.getvalue()){
                queue.addFirst(node);
            }
            //Otherwise we will go through the queue until we find in the right place to append the node
            for(Pair queueNode : this.queue){
                //If this is the last node of the queue and it is smaller than the node to be inserted,
                // then it is appended to the end of the queue
                if(queue.getLast() == queueNode && queueNode.getvalue() < node.getvalue()){
                    queue.addLast(node);

                }


                /*If the node to be inserted is bigger than the current node of the queue AND
                 *it is smaller than the next node of the queue, then we append at he position of the next
                 *node of the queue.*/

                int nextNodevalue = queue.get(queue.indexOf(queueNode)+1).getvalue();

                if(node.getvalue() > queueNode.getvalue() && node.getvalue() < nextNodevalue){
                    queue.add(queue.indexOf(queueNode),node);

                }

            }
        }

    }





    ////JAVAFX RELATED METHODS/////////

    public void runAnotherApp(Class<? extends Application> anotherAppClass) throws Exception {
        Application app2 = anotherAppClass.newInstance();
        Stage anotherStage = new Stage();
        StackPane root = new StackPane();
        final Scene scene = new Scene(root, 1080, 1920);
        anotherStage.setScene(scene);
        app2.start(anotherStage);
    }

    public void alert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Content here", ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }
    public class RunLoading implements Runnable{

        @Override
        public void run() {
            try {
                runAnotherApp(loading.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




   ///////////////GENERIC CLASS METHODS//////////////

    /**
     * This method will get the last item name of the queue and insert it into the back of the queue
     */
    public String getNext(){
        Pair last_node = queue.removeFirst();
        queue.add(last_node);
        return last_node.getname();
    }

    public Boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Pair> getQueue(){
        return this.queue;
    }

}
