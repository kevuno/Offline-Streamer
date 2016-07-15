import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sun.awt.image.ImageWatched;

import javax.swing.*;
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

    /**A status variable of the manager that will be true only when the Manager is setup for the first time**/
    private Boolean firstRun;


    /**
     * Constructs the Manager with a filename, which inside has the list of the videos.
     * @param thatListFilename: The filename with the list
     */
    public VideoManager(String thatListFilename){
        listFilename = thatListFilename;
        queue = new LinkedList<>();
    }


    /**
     * Initiate the manager depending of the status of the WebSocket. If it is not active after
     * trying 15 seconds later, use the setup from file
     */


    public void initManager() throws FileNotFoundException, UnsupportedEncodingException {
        this.firstRun = true;
        System.out.println("WS ACTIVE? " +MyWebSocketHandler.isActive());
        //Wait 5 initial seconds for the Web Socket to start
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!MyWebSocketHandler.isActive()){
            //Try again 10 seconds later
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Finally if it was not active after 15 seconds of waiting, then just set up from local list
            System.out.println("WS ACTIVE? " +MyWebSocketHandler.isActive());
            if(!MyWebSocketHandler.isActive()){
                setupLocal();
            }
        }
    }

    /**
     * First time running the program, it will be set up using this method
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setupLocal() throws FileNotFoundException, UnsupportedEncodingException {

        //Check if the list file and videos directory file exists, if not it will be created.
        if(checkAndCreateDirs()){
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
        //Since this
        firstRun = false;
    }



    /**
     * Changes the queue from a Message obtained by the WS.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setupWs(String codedMessage) throws FileNotFoundException, UnsupportedEncodingException {

        //Check if the list file and videos directory file exists, if not it will be created.
        if(checkAndCreateDirs()) {
            System.out.println("Setting up from  setupWs");
            //Obtain parsed message from a codedMessage
            ParsedMessage listOfFilesData = new ParsedMessage(codedMessage);

            LinkedList<Video> existQueue = new LinkedList<>();
            LinkedList<Video> nonExistQueue = new LinkedList<>();

            for (VideoFileData videoData : listOfFilesData.getVideoFileDataList()) {
                //Get data and put into queues
                String name = videoData.getName();
                Integer position = videoData.getPosition();
                String url = videoData.getUrl();
                Video node = new Video(name, position, url);

                //Add the node to the respective queue, depending on whether it is found in the directory.
                if (!new File("videos/" + name + ".mp4").exists()) {
                    nonExistQueue.add(node);
                } else {
                    existQueue.add(node);
                }
            }
            //Before downloading, list file is rewritten with the files that were found in the new list
            try {
                PrintWriter writerFIle = new PrintWriter("videos/" + listFilename, "UTF-8");
                for (Video node : existQueue) {
                    System.out.println("PUTIING IN FILE A "+node);
                    writerFIle.println(node.getname() + "," + node.getUrl());
                }
                writerFIle.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("PREEEE download");
            //If no files are missing, then no need to download anything
            if (!nonExistQueue.isEmpty()) {
                System.out.println("Non exist queue is not empty so there are files missing");
                queue = Download(nonExistQueue, existQueue);
            } else if (firstRun) {
                //Set the queue only if this is the first time that the Manager was set
                queue = existQueue;

            }

            //The firstRun for setting the Manager is over, so it is set up to false.
            firstRun = false;
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

        if(existQueue.isEmpty()) {

            alert("No se encontro ningun video en la carpeta videos. La descarga esta comenzando...");
        }
        //----DOWNLOAD  first item out of the queue and download it while the alert is shown.----//

        Video first_node = noExistQueue.removeFirst();
        FileDownloader downloader = new FileDownloader(first_node.getname(),first_node.getUrl());
        System.out.println("predownload");
        if(downloader.downloadFromFile()){
            System.out.println("download response");
            //Add to the exist queue, since now there is a file in the directory.
            existQueue.add(first_node);
        }
        //Now move all the nodes with the same name as the first into the existQueue, since the file now exists.
        //Instead of removing, just add the nodes that are not equal to the first node into a new list
        // and then set it equal to noExist queue
        LinkedList<Video> tempNoExistQueue = new LinkedList<>();
        for(Video node : noExistQueue){
            if(node.getname().equals(first_node.getname())){
                existQueue.add(node);

            }else{
                tempNoExistQueue.add(node);
            }

        }
        noExistQueue = tempNoExistQueue;


        //------Start the missing files downloads.------//
        Thread downloads = new Thread(new FileDownloader(noExistQueue));
        downloads.start();

        //Return only the queue with the files that existed + were downloaded
        return existQueue;
    }

    public Boolean checkAndCreateDirs(){
        Boolean dirCreate = true;
        Boolean listCreate = true;
        //Check videos directory
        File videosDir = new File("videos/");
        if(!videosDir.exists()){
            if(videosDir.mkdir()){
                System.out.println("Videos directory successfully created");
            }else{
                System.out.println("Videos directory was not created");
                dirCreate = false;
            }
        }

        //Check list file directory
        File listFile = new File("videos/"+listFilename);
        if(!listFile.exists()){
            try {
                if (listFile.createNewFile()) {
                    System.out.println("List file successfully created");
                }else{
                    System.out.println("List file was not created");
                    listCreate = false;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return dirCreate && listCreate;
    }






    /**
     * This method will be triggered when the FileDownloader finishes downloading files from a queue.
     * As well, as the files are being downloaded, and
     * @param node: The node from the FileDownloader to be added
     */
    public static void downloadQueueListener(Video node){
        try{
            System.out.println("CALL to add node " + node);
            addNode(node);
        }catch(ConcurrentModificationException e ){
            System.out.println("Modifying queue from Filedownloader");
        }

        //Before downloading, list file is rewritten with the new list
        try {
            PrintWriter writerFIle = new PrintWriter("videos/" + listFilename, "UTF-8");
            writerFIle.println(node.getname() + "," + node.getUrl());
            writerFIle.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }



    /**
     * Adds the given node to the queue according to the position given by the node
     * @param node: The node to be added to the queue
     */
    public static Boolean addNode(Video node){
        //TODO SOLVE ADDING DUPLICATES OF THE SAME NODE WHEN ONLY ONE NEEDS TO BE ADDED
        //Append to the beginning if queue is empty
        if(queue.isEmpty()){
           queue.addFirst(node);

        }else{

            //If the first node of the queue is bigger than the value of the node to be inserte
            //then we append the node at the front of the queue
            if(queue.get(0).getvalue() > node.getvalue()){
                queue.addFirst(node);
            }


            //Otherwise we will go through the queue until we find in the right place to append the node

            //Add nodes into a visited list
            ArrayList<Integer> visited = new ArrayList<>();
            for(Video queueNode : queue){
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


    /**
     * Display a JPane alert
     * @param message: The message to be displayed in the aler
     */
    public void alert(String message){
        System.out.println("Showing alert");
        Thread alert = new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(null, message);
            }
        });
        alert.start();
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
