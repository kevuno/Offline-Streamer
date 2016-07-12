import java.util.Comparator;

/**
 * Created by Kevin Bastian
 */
public class Video implements Comparator<Video> {
    private String name;
    private Integer value;
    private String url;

    //Empty constructor for the sorter
    public Video(){

    }

    public Video(String name, int value, String url) {
        this.name = name;
        this.value = value;
        this.url = url;

    }
    public String getname(){
        return name;
    }
    public int getvalue() {
        return value;
    }

    public String getUrl() {
        return url;
    }
    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public int compare(Video o1, Video o2) {
        if(o1.getvalue() < o2.getvalue()){
            return -1;
        }else if(o1.getvalue() == o2.getvalue()){
            return 0;
        }else if (o1.getvalue() > o2.getvalue()){
            return 1;
        }
        return 0;
    }
}
