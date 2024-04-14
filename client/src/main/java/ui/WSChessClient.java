package ui;

import model.AuthData;
import model.UserData;
import model.GameData;
import java.util.Collection;


public class WSChessClient {
        private final ServerFacade serverFacade;
        private final WebSocketFacade webSocketFacade;
        private AuthData userToken;

    public WSChessClient(String URL, NotificationHandler notificationHandler) throws ResponseException {
            this.serverFacade = new ServerFacade(URL);
            this.webSocketFacade = new WebSocketFacade(URL, notificationHandler);

        }

    public boolean login(String username, String password) throws Exception {
        UserData userData = new UserData(username, password, null);
        this.userToken = serverFacade.login(userData);

        return this.userToken != null && this.userToken.authToken() != null;
    }

    public boolean register(String username, String password, String email) throws Exception {
        UserData userData = new UserData(username, password, email);
        this.userToken = serverFacade.register(userData);
        return this.userToken != null && this.userToken.authToken() != null;
    }

    public void logout() throws Exception {
        serverFacade.logout(this.userToken.authToken());
        this.userToken = null;
    }

    public Collection<GameData> listGames() throws Exception {
        return serverFacade.listGames(this.userToken.authToken());
    }

    public GameData createGame(String gameName) throws Exception {
        GameData newGame = new GameData(0,null,null, gameName);
        GameData createdGame = serverFacade.createGame(userToken.authToken(), newGame);
        return createdGame;
    }

    public void joinGame(int gameId, String playerType) throws Exception {
        serverFacade.joinGame(this.userToken.authToken(), gameId, playerType);
    }
}
