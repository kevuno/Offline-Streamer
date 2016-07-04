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

    /**The queue of the program**/
    private LinkedList<String> queue;

    /**The filename of the list for the queue**/
    private String listFilename;

    public VideoManager(String listFilename){
        this.listFilename = listFilename;
        //Lista inicial
        //LinkedList<String> queue = new LinkedList<>();
        //Test queue
        //queue.add("test1");
        //this.queue = queue;
    }

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
            //Creating queue
            LinkedList<String> queue = new LinkedList<>();
            ExecutorService es = Executors.newCachedThreadPool();
            while (input.hasNextLine()){
                String linea = input.nextLine();
                String vidName = linea.trim();

                System.out.println(linea);

                String completeVidName = vidName+".mp4";
                if(!new File("videos/"+completeVidName).exists()){

                    //Place the downloader in a

                    Thread downloader = new Thread(new FileDownloader(completeVidName));
                    downloader.start();
                    //FileDownloader f = new FileDownloader(completeVidName);
                    //f.download();

                    try {
                        //Thread loading = new Thread(new FileDownloader(completeVidName));
                        //loading.start();
                        //runAnotherApp(loading.class);
                        alert();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Wait for the file to finish downloading
                    try {
                        downloader.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    //Check again if file is there
                    if(new File("videos/"+completeVidName).exists()){
                        queue.add(vidName);

                    }else{
                        System.out.println("File doesn't appear to be in videos directory after attempting to download");
                    }
                }else{
                    queue.add(vidName);
                }
            }
            this.queue = queue;







        }
    }



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



    /**
     * Allows the WebSocketHandler to send in the name of the files, given by their name and code, to
     * then get downloaded if they do not exist in the videos directory and finally adds them into the
     * queue.
     * @param files: The string containing the files and their respective codes.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void setup(String files) throws FileNotFoundException, UnsupportedEncodingException {
        //Read from the list filename and check
        //si algun archivo de la lista no existe que lo descargue
        //Si no se pudo descargar no se agrega al queue ni al archivo

        File file =  new File("videos/"+listFilename);

        if(!file.exists()){
            System.out.println("Falta archivo de lista de videos, creando uno nuevo...");

            PrintWriter writerFIle = new PrintWriter("videos/"+listFilename, "UTF-8");

            //writerFIle.println();
            writerFIle.close();


        }
        //Get the filenames into an arrayList
        String[] fa = files.split("-");
        ArrayList<String> filesArray  = new ArrayList<String>(Arrays.asList(fa));

        //Loop through the array to check and download if necessary

        for (String vidName: filesArray){
            String completeVidName = vidName+".mp4";
            if(!new File("videos/"+completeVidName).exists()){

                try {

                    URL link = new URL("http://www.micro-tec.com.mx/samtec/videos"+completeVidName);

                    ReadableByteChannel rbc = Channels.newChannel(link.openStream());
                    FileOutputStream fos = new FileOutputStream("videos/"+completeVidName);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);


                } catch (IOException e){
                    System.out.println("Unable to download file");
                    e.printStackTrace();
                }

                //Check again if file is there
                if(new File("videos/"+completeVidName).exists()){
                    queue.add(vidName);

                }else{
                    System.out.println("File doesn't appear to be in videos directory after attempting to download");
                }

            }else{
                queue.add(vidName);
            }

        }

        //Finally pass new queue into fila.txt



    }

    public void QueueIntoFile(){

    }

    public LinkedList<String> getQueue(){
        return this.queue;
    }

}
