/**
 * Created by kevin
 */
public class VideoFileData {
    String url;

    String name;

    Integer position;

    public VideoFileData(String url, String name, Integer position){
        this.url = url;
        this.name = name;
        this.position = position;

    }

    public String getUrl(){
        return this.url;
    }

    public String getName(){
        return this.name;
    }

    public Integer getPosition(){
        return this.position;
    }

}
