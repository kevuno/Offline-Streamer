/**
 * Created by kevin
 */

import javafx.scene.media.MediaException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.net.InetSocketAddress;


import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class WebSocketTest implements Runnable{
    static int run = 0;
    static WebSocketClient client;
    static URI uri;

    public void run()
    {
        //Set up the connection
        uri = URI.create("ws://187.157.170.85:9000/");
        client = new WebSocketClient();
        connect(client,uri);

    }

    public static void connect(WebSocketClient client, URI uri){
        run++;
        try{
            try{
                //Start client

                client.start();
                // The socket that receives events

                MyWebSocketHandler socket = new MyWebSocketHandler();
                // Attempt Connect

                Future<Session> fut = client.connect(socket, uri);
                // Wait for Connect
                Session session = fut.get();

                //Save session and uri into te handler so that it can try to reconnect
                socket.setClientUri(client,uri);

                // Send a test msg
                session.getRemote().sendString("MSG|Kevin|Saludos|Que onda hugo");


                // Close session
                //session.close();


            }catch (Exception e) {
                System.out.println(e);
                System.out.println(run);
                connect(client, uri);
                Thread.sleep(5000);

            }
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }

    }
}