import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

@WebSocket
public class MyWebSocketHandler {
    private WebSocketClient client;
    private URI uri;

    private static boolean active;


    @OnWebSocketClose
    public void onClose(int statusCode, String reason) throws InterruptedException {
        active = false;
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        WebSocketTest.connect(client,uri);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws InterruptedException {
        active = true;
        System.out.println("Trying to Connect to: " + session.getRemoteAddress().getAddress());
        try {
            session.getRemote().sendString("REG|videosws|samtec");
            session.getRemote().sendString("MSG|videosws|videosws|U");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {


        System.out.println("Message: " + message);


        String[] Array = message.split("\\|");
        if(Array[0].equals("VID")){
            System.out.println();
            //Leave only the data array by removing the first two elements of the original message.
            ArrayList<String> dataArray = new ArrayList<>(Arrays.asList(Array));
            dataArray.subList(0,2).clear();
            String finalCodedMsg = String.join("|",dataArray);
            try{
                Player.setManager(finalCodedMsg);
            }catch (Exception e){
                e.printStackTrace();
            }



        }

    }

    public void setClientUri(WebSocketClient client, URI uri){
        this.client = client;
        this.uri = uri;
    }

    public static Boolean isActive(){
        return active;
    }
}
