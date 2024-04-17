package server;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Notification;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, Integer gameID, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen() && !c.authToken.equals(excludeAuthToken) && Objects.equals(c.gameID, gameID)) {
                c.send(new Gson().toJson(notification));
            }
//            } else if (!c.session.isOpen()) {
//                removeList.add(c);
//            }
//        }
//        removeList.forEach(c -> connections.remove(c.userName));
    }
    }
}
