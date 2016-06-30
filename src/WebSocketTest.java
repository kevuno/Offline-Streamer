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

public class WebSocketTest{

    public static void main(String[] args)
    {
        //Set up the connection
        URI uri = URI.create("ws://187.157.170.85:9000/");
        WebSocketClient client = new WebSocketClient();
        connect(client,uri);
        player.main(null);

    }

    public static void connect(WebSocketClient client, URI uri){

        try
        {
            try
            {
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
                session.getRemote().sendString("MSG|hugo|Saludos|Que onda hugo");


                // Close session
                //session.close();
            }catch (MediaException m){
                System.out.println("File not found");

            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }

    }
}