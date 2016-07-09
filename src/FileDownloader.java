import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by kevin
 */
public  class FileDownloader implements Runnable {

    String filename;
    LinkedList<Pair> queue;

    /**
     * The constructor that will save the queue
     *
     * @param queue
     */
    FileDownloader(LinkedList<Pair> queue) {
        this.queue = queue;
    }

    /**
     * The constructor that will save the filename
     *
     * @param filename
     */
    FileDownloader(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {

        if (filename == null && queue != null) {
            downloadFromQueue();
            System.out.println("FInished downloading queue");
        }else if(queue == null && filename !=null){
            downloadFromFile();
            System.out.println("FInished downloading file");
        }else {
            System.out.println("Null values in both filename and queue, nothing was downloaded");
        }

    }

    public Boolean downloadFromFile() {

        String completeVidName = filename+".mp4";
        try {
            System.out.println("Starting to download " + completeVidName);
            URL link = new URL("http://www.micro-tec.com.mx/samtec/videos/" + completeVidName);

            ReadableByteChannel rbc = Channels.newChannel(link.openStream());
            FileOutputStream fos = new FileOutputStream("videos/" + completeVidName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            System.out.println("Download completed");

            return true;

        } catch (IOException e) {
            System.out.println("Unable to download file");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public ArrayList<Pair> downloadFromQueue() {
        //TODO do not atempt download if was already downloaded or attempted to download
        //A list with the names of the filed files to were not downloaded successfully.
        ArrayList<Pair> completed = new ArrayList<>();

        for (Pair node : queue){
            String completeVidName = node.getname()+".mp4";
            try {
                System.out.println("Starting to download " + completeVidName);
                URL link = new URL("http://www.micro-tec.com.mx/samtec/videos/" + completeVidName);

                ReadableByteChannel rbc = Channels.newChannel(link.openStream());
                FileOutputStream fos = new FileOutputStream("videos/" + completeVidName);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                //IF THE CODE GETS TO THIS PART WITHOUT ANY EXCEPTION, IT MEANS THAT THE DOWNLOAD WAS COMPLETED
                System.out.println("Download completed");
                completed.add(node);

                //Add to the fails list in case of any exception
            } catch (IOException e) {
                System.out.println("Unable to download file");
            } catch (Exception e) {
                System.out.println("Unable to download file");
                e.printStackTrace();
            }

        }
        return completed;
    }



}
