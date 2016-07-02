/**
 * Created by kevin
 */
public class Main {
    public static void main(String[] args){
        Thread t1 = new Thread(new player());

        Thread t2 = new Thread(new WebSocketTest());

        t1.start();
        t2.start();

    }



}
