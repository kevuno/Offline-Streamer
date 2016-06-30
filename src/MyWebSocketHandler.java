import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

@WebSocket
public class MyWebSocketHandler {
    WebSocketClient client;
    URI uri;



    @OnWebSocketClose
    public void onClose(int statusCode, String reason) throws InterruptedException {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        WebSocketTest.connect(client,uri);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws InterruptedException {

        System.out.println("Trying to Connect to: " + session.getRemoteAddress().getAddress());
        try {
            session.getRemote().sendString("REG|hugo|samtec");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {


        System.out.println("Message: " + message);


        String[] Array1 = message.split("\\|");
        if(Array1[0].equals("MSG")){
            String[] Array = Array1[1].split(",");
            ArrayList<String> dataArray = new ArrayList<String>(Arrays.asList(Array));
            LinkedList<String> queue = new LinkedList<>();
            for (String element: dataArray) {
                queue.add(element);
            }

            //queue.add("test4");
            //queue.add("test3");
            //queue.add("test1");

            player.setManager("file1-file2");
        }

    }

    public void setClientUri(WebSocketClient client, URI uri){
        this.client = client;
        this.uri = uri;
    }

}