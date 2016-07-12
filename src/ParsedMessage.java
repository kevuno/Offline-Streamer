/**
 * Created by Kevin Bastian
 *
 * Parses a base64 coded message with multiple lines delimited by one character.
 * Each line will be a set of data delimited by a second character
 */
//BASE 64 libraries
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;


public class ParsedMessage {

    ArrayList<VideoFileData> filesDataList;

    /**
     * Construct object by spliting the message by first delimiter, decoding with base64 each partition,
     * and finally split each partition to obtain the data found inside.
     * @param message The message to be parsed
     * @param delimiter1 The delimiter that will split into base64 string tokens.
     * @param delimiter2 The delimiter that will split each token to obtain the data.
     */
    public ArrayList<VideoFileData> ParsedMessage(String message, String delimiter1, String delimiter2){
        filesDataList = new ArrayList<>();

        String[] base64Array = message.split(delimiter1);

        for(String base64Token : base64Array){
            byte[] dataInBytes = DatatypeConverter.parseBase64Binary(base64Token);
            String[] dataArray = DatatypeConverter.printBase64Binary(dataInBytes).split(delimiter2);

            //Create a VideoFileData object
            //Index = 0 is name, ind = 1 is name is name in server, ind = 2 is position
            VideoFileData parsedObject = new VideoFileData(dataArray[0],dataArray[1],Integer.parseInt(dataArray[2]));
            filesDataList.add(parsedObject);

        }

        return filesDataList;






    }
}
