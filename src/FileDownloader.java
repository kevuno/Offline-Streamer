import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by kevin
 */
public class FileDownloader implements Runnable {
    String completeVidName;

    FileDownloader(String completeVidName){
        this.completeVidName = completeVidName;

    }


    @Override
    public void run() {
        this.download();
    }

    public  void download(){
        try {
            System.out.println("Starting to download " + completeVidName);
            URL link = new URL("http://www.micro-tec.com.mx/samtec/videos/"+completeVidName);

            ReadableByteChannel rbc = Channels.newChannel(link.openStream());
            FileOutputStream fos = new FileOutputStream("videos/"+completeVidName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);


            System.out.println("Download completed");






        } catch (IOException e){
            System.out.println("Unable to download file");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
