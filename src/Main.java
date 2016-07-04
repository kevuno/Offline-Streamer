/**
 * Created by kevin
 */
public class Main {
    public static void main(String[] args){
        //Creating player thread
        Thread t1 = new Thread(new Player());
        //Creating Websocket thread
        Thread t2 = new Thread(new WebSocketTest());
        //Run both threads
        t1.start();
        t2.start();

    }



}
