package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public Integer gameID;


    public Connection(String authToken, Integer gameID, Session session) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
