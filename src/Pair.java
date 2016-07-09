/**
 * Created by Kevin Bastian
 */
public class Pair{
    private String name;
    private Integer value;

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

    public int compare(Pair o1, Pair o2) {
        Pair a1 = (Pair)o1;
        Pair a2 = (Pair)o2;
        if(a1.value>a2.value) {
            return 1;
        }
        else if(a1.value<a2.value) {
            return -1;
        }
        return 0;

    }
}
