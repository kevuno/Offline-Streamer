import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * Created by Kevin Bastian
 *
 * This class is in charge of keeping the queue for the Video Player in order by downloading files that are missing
 * and receiving information from the WebSocket.
 */

public class VideoManager {

    /**The queue of the program, it has a pair of values a name and the original position of the queue**/
    private volatile static LinkedList<Video> queue;

    /**The filename of the list for the queue**/
    private static String listFilename;

    /**
     * Constructs the Manager with a filename, which inside has the list of the videos.
     * @param thatListFilename: The filename with the list
     */
    public VideoManager(String thatListFilename){
        listFilename = thatListFilename;
        queue = new LinkedList<>();
    }


    /**
     * First time running the program, it will be set up using this method
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setup() throws FileNotFoundException, UnsupportedEncodingException {

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
            LinkedList<Video> existQueue    = new LinkedList<>();
            //This queue will only contain the names from the list file that don't exist in the videos dir.
            LinkedList<Video> nonExistQueue = new LinkedList<>();

            /*Go through the names in the file and add to the corresponding queues */

            Integer position_count = 0;
            while (input.hasNextLine()){

                //FORMAT:  name,url
                String line = input.nextLine().trim();
                String[] csv = line.split(",");

                String name = csv[0]; String url = csv[1];
                String completeVidName = csv[0]+".mp4";
                //Create node to insert in queue
                Video node = new Video(name,position_count,url);

                //Add the node to the respective queue, depending on whether it is found in the directory.
                if(!new File("videos/"+completeVidName).exists()){
                    nonExistQueue.add(node);
                }else{
                    existQueue.add(node);
                }
                position_count++;
            }
            input.close();

            //If no files are missing, then no need to download anything
            if(!nonExistQueue.isEmpty()){
                queue = Download(nonExistQueue,existQueue);
            }else{
                queue = existQueue;
            }

        }
    }



    /**
     * Changes the queue from a Message obtained by the WS.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setup(String codedMessage) throws FileNotFoundException, UnsupportedEncodingException {
        //Check if the list file exists in the videos directory, if not it will be created

        File file =  new File("videos/"+listFilename);
        if(!file.exists()){
            System.out.println("Falta archivo de lista de videos, creando uno nuevo...");
            PrintWriter writerFIle = new PrintWriter("videos/"+listFilename, "UTF-8");
            writerFIle.close();
        }else{

            //Obtain parsed message from a codedMessage
            ParsedMessage listOfFilesData = new ParsedMessage(codedMessage);

            LinkedList<Video> existQueue    = new LinkedList<>();
            LinkedList<Video> nonExistQueue = new LinkedList<>();

            for(VideoFileData videoData : listOfFilesData.getVideoFileDataList()){
                //Get data and put into queues
                String name      = videoData.getName();
                Integer position = videoData.getPosition();
                String url       = videoData.getUrl();
                Video node = new Video(name,position,url);

                //Add the node to the respective queue, depending on whether it is found in the directory.
                if(!new File("videos/"+name+".mp4").exists()){
                    nonExistQueue.add(node);
                }else{
                    existQueue.add(node);
                }
            }
            //If no files are missing, then no need to download anything
            if(!nonExistQueue.isEmpty()){
                queue = Download(nonExistQueue,existQueue);
            }

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

    public LinkedList<Video> Download(LinkedList<Video> noExistQueue, LinkedList<Video> existQueue){

        if(existQueue.isEmpty()){

            //Alert and download only first file
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se encontró ningun video, descargando", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();


            //Get first item out of the queue and download it while the alert is shown.
            Video first_node = noExistQueue.removeFirst();
            FileDownloader downloader = new FileDownloader(first_node.getname(),first_node.getUrl());
            if(downloader.downloadFromFile()){
                //Add to the exist queue, since now there is a file in the directory.
                existQueue.add(first_node);
            }
            //Once it finishes hide the alert
            alert.hide();

        }

        //Start the missing files downloads.
        Thread downloads = new Thread(new FileDownloader(noExistQueue));
        downloads.start();

        //Return only the queue with the files that existed + were downloaded
        return existQueue;
    }



















    /**
     * This method will be triggered when the FileDownloader finishes downloading files from a queue
     * @param node: The node from the FileDownloader to be added
     */
    public static void downloadQueueListener(Video node){
        try{
            addNode(node);
        }catch(ConcurrentModificationException e ){
            System.out.println("Modifying queue from Filedownloader");
        }

    }



    /**
     * Adds the given node to the queue according to the position given by the node
     * @param node: The node to be added to the queue
     */
    public static Boolean addNode(Video node){
        //Append to the beginning if queue is empty
        if(queue.isEmpty()){
           queue.addFirst(node);

        }else{

            //If the first node of the queue is bigger than the value of the node to be inserte
            //then we append the node at the front of the queue
            if(queue.get(0).getvalue() > node.getvalue()){
                System.out.println("Adding at the beginning");
                queue.addFirst(node);
            }


            //Otherwise we will go through the queue until we find in the right place to append the node

            //Add nodes into a visited list
            ArrayList<Integer> visited = new ArrayList<>();
            for(Video queueNode : queue){
                System.out.println(queueNode);
                //Check if the current node is the last node
                if(queue.getLast() == queueNode){
                    //If this is the last node of the queue and it is smaller than the node to be inserted,
                    // then it is appended to the end of the queue
                    if(queueNode.getvalue() < node.getvalue()){
                        queue.addLast(node);
                        return true;
                    }
                }else{
                    /*If the node to be inserted is bigger than the current node of the queue AND (
                    *it is smaller than the next node of the queue, or the next node of the queue is
                    * equal to the first node of the queue then we append at the position of the next
                    * (in the middle)
                    *node of the queue.*/

                    int nextNodevalue = queue.get(queue.indexOf(queueNode)+1).getvalue();
                    int firstNodevalue = getTrulyFirst().getvalue();
                    int lastNodevalue = getTrulyLast().getvalue();


                    if(node.getvalue() > queueNode.getvalue() && node.getvalue() < nextNodevalue){
                        queue.add(queue.indexOf(queueNode),node);
                        return true;
                    }else if (firstNodevalue == nextNodevalue && !visited.contains(lastNodevalue)){
                        //Add if the next value is the smallest value but only if the for loop has not
                        // visited the largest value of the queue.
                        queue.add(queue.indexOf(queueNode)+1,node);
                    }
                }
                visited.add(node.getvalue());
            }

        }
        return false;

    }





    ////JAVAFX RELATED METHODS/////////
    //TODO: make this less spaggetiy.
    public void runAnotherApp(Class<? extends Application> anotherAppClass) throws Exception {
        Application app2 = anotherAppClass.newInstance();
        Stage anotherStage = new Stage();
        StackPane root = new StackPane();
        final Scene scene = new Scene(root, 1080, 1920);
        anotherStage.setScene(scene);
        app2.start(anotherStage);
    }



   ///////////////GENERIC CLASS METHODS//////////////

    /**
     * Get the smallest node of the queue
     */
    public static Video getTrulyFirst(){
        LinkedList<Video> temp = new LinkedList<>();
        for (Video node : queue){
            temp.add(node);
        }
        Collections.sort(temp,new Video());

        return temp.getFirst();

    }
    /**
     * Get the largest node of the queue
     */
    public static Video getTrulyLast(){
        LinkedList<Video> temp = new LinkedList<>();
        for (Video node : queue){
            temp.add(node);
        }
        Collections.sort(temp,new Video());

        return temp.getLast();

    }


    /**
     * This method will get the last item name of the queue and insert it into the back of the queue
     */
    public String getNext(){
        Video last_node = queue.removeFirst();
        queue.add(last_node);
        return last_node.getname();
    }

    public Boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Video> getQueue(){
        return queue;
    }

}
