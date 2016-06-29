/**
 * Created by kevin
 */

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
        URI uri = URI.create("ws://187.157.170.85:9000/");

        WebSocketClient client = new WebSocketClient();
        try
        {
            try
            {

                    client.start();
                    // The socket that receives events
                    MyWebSocketHandler socket = new MyWebSocketHandler();
                    // Attempt Connect
                    Future<Session> fut = client.connect(socket, uri);
                    // Wait for Connect

                    Session session = fut.get();
                    // Send a message

                    session.getRemote().sendString("MSG|hugo|Saludos|Que onda hugo");

                    System.out.println("heyy");
                    LinkedList<String> queue = new LinkedList<>();
                    queue.add("test4");
                    player.updateQueue(queue);
                    player.main(null);


                    // Close session
                    //session.close();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }
}