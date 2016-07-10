import java.util.Comparator;

/**
 * Created by Kevin Bastian
 */
public class Pair implements Comparator<Pair> {
    private String name;
    private Integer value;

    public Pair(){

    }

    public Pair(String name, int value) {
        this.name = name;
        this.value = value;

    }
    public String getname(){
        return name;
    }
    public int getvalue() {
        return value;
    }

    @Override
    public String toString(){
        return this.name;
    }


    @Override
    public int compare(Pair o1, Pair o2) {
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
