/**
 * Created by Kevin Bastian
 *
 * Parses a base64 coded message with multiple lines delimited by one character.
 * Each line will be a set of data delimited by a second character
 */
//BASE 64 libraries
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;


public class ParsedMessage {

    private ArrayList<VideoFileData> filesDataList;

    /**
     * Construct object by spliting the message by first delimiter, decoding with base64 each partition,
     * and finally split each partition to obtain the data found inside.
     * @param message The message to be parsed
     */
    public ParsedMessage(String message){
        //Final arrayList with the message
        final ArrayList<VideoFileData>filesDataListFromMessage = new ArrayList<>();
        String[] base64Array = message.split("\\|");
        ArrayList<String> base64ArrayList = new ArrayList<>(Arrays.asList(base64Array));
        for(String base64Token : base64ArrayList){
            //Decode and convert bytes to string to then obtain data
            byte[] decoded = Base64.getDecoder().decode(base64Token);
            String[] dataArray = new String(decoded).split("\\|");
            ArrayList<String> decodedArrayList = new ArrayList<>(Arrays.asList(dataArray));
            System.out.println(decodedArrayList);
            //Create a VideoFileData object and add to list
            //Index = 0 is url, ind = 1 is name is name, ind = 2 is position
            String url = decodedArrayList.get(0);
            String name = decodedArrayList.get(1);
            Integer position = Integer.parseInt(decodedArrayList.get(2));
            VideoFileData parsedObject = new VideoFileData(url,name,position);
            filesDataListFromMessage.add(parsedObject);
        }
        this.filesDataList = filesDataListFromMessage;
    }

    public ArrayList<VideoFileData> getVideoFileDataList(){
        return this.filesDataList;
    }

}
