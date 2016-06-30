import java.util.LinkedList;

/**
 * Created by kevin
 */
public class VideoManager {

    /**The queue of the program**/
    private LinkedList<String> queue;

    public VideoManager(){
        //Lista inicial
        LinkedList<String> queue = new LinkedList<>();
        //Test queue
        queue.add("test1");
        this.queue = queue;
    }

    public void setup(String filenameList){
        //Read from the list filename and check
        //si algun archivo de la lista no existe que lo descargue
        //Si no se pudo descargar no se agrega


    }
    public LinkedList<String> getQueue(){
        return this.queue;
    }

}
