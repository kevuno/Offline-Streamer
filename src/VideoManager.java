import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

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

        if(!file.exists()){
            System.out.println("Falta archivo de lista de videos, creando uno nuevo...");

            PrintWriter writerFIle = new PrintWriter("videos/"+listFilename, "UTF-8");

            writerFIle.close();

        }else{
            //Open file and read to insert into queue
            File listFile = new File("videos/"+listFilename);
            Scanner input = new Scanner(listFile);
            //Creating queue
            LinkedList<String> queue = new LinkedList<>();
            while (input.hasNextLine()){
                String linea = input.nextLine();
                linea = linea.trim();
                System.out.println(linea);
                queue.add(linea);
            }
            this.queue = queue;
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
            if(!new File("videos/"+vidName+".mp4").exists()){
                //TODO: download file into /videos
                //if downloaded: add to queue
            }else{
                queue.add(vidName);
            }


        }



    }

    public void QueueIntoFile(){

    }

    public LinkedList<String> getQueue(){
        return this.queue;
    }

}
