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

    private String filename;
    private String url;
    private LinkedList<Video> queue;
    private VideoManager manager;

    /**
     * The constructor that will save the queue
     *
     * @param queue
     */
    FileDownloader(LinkedList<Video> queue) {
        this.queue = queue;
    }

    /**
     * The constructor that will save the filename
     *
     * @param filename
     */
    FileDownloader(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }



    @Override
    public void run() {
        if (filename == null && queue != null) {
            downloadFromQueue();
        }else if(queue == null && filename !=null){
            downloadFromFile();
        }else {
            System.out.println("Null values in both filename and queue, nothing was downloaded");
        }


    }
    public Boolean downloadFromFile() {

        String completeVidName = filename+".mp4";
        try {
            System.out.println("Starting to download " + completeVidName);
            URL link = new URL(url + completeVidName);

            ReadableByteChannel rbc = Channels.newChannel(link.openStream());
            FileOutputStream fos = new FileOutputStream("videos/" + completeVidName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();

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
    public void downloadFromQueue() {
        //A list with the names of the filed files to were not downloaded successfully.
        ArrayList<Video> completed = new ArrayList<>();

        for (Video node : queue){
            //Do not download if it has been downloaded already
            if(!completed.contains(node)){
                String completeVidName = node.getname()+".mp4";
                String url = node.getUrl();
                try {
                    //SETUP
                    System.out.println("Starting to download " + completeVidName);
                    URL link = new URL(url + completeVidName);

                    //DOWNLOAD
                    ReadableByteChannel rbc = Channels.newChannel(link.openStream());
                    FileOutputStream fos = new FileOutputStream("videos/" + completeVidName);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    //IF THE CODE GETS TO THIS PART WITHOUT ANY EXCEPTION, IT MEANS THAT THE DOWNLOAD WAS COMPLETED
                    System.out.println("Download completed");
                    fos.close();
                    //Add to the completed list
                    completed.add(node);
                    //Pass the completed node to the VideoManager
                    VideoManager.downloadQueueListener(node);

                    //Add to the fails list in case of any exception
                } catch (IOException e) {
                    System.out.println("Unable to download file "+ node.getname());
                } catch (Exception e) {
                    System.out.println("Unable to download file "+ node.getname());
                    e.printStackTrace();
                }
            }else{
                //Still add to the final list since it has to be added to the queue
                completed.add(node);
            }


        }

    }



}
