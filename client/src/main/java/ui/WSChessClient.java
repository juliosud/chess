package ui;

import model.AuthData;
import model.UserData;
import model.GameData;
import webSocketMessages.userCommands.Leave;


import java.util.Collection;
import java.util.List;
import java.util.Map;


public class WSChessClient {
        //private final ServerFacade serverFacade;
        private final ServerFacadeNew serverFacade;
        private final WebSocketFacade webSocketFacade;
        private AuthData userToken;

    public WSChessClient(String URL, NotificationHandler notificationHandler) throws ResponseException {
        //serverFacade = new ServerFacade(URL);
        serverFacade = new ServerFacadeNew(URL); // Use ServerFacadeNew
        webSocketFacade = new WebSocketFacade(URL, notificationHandler);
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
        //return serverFacade.listGames(this.userToken.authToken());
        return List.of(serverFacade.listGames(this.userToken.authToken()));
    }

    public Map<String, Integer> createGame(String gameName) throws Exception {
        GameData newGame = new GameData(0,null,null, gameName);
        //GameData createdGame = serverFacade.createGame(userToken.authToken(), newGame);
        Map<String, Integer> createdGame = serverFacade.createGame(userToken.authToken(), newGame);
        return createdGame;
    }

    public void joinGame(int gameId, String playerType) throws Exception {
        serverFacade.joinGame(this.userToken.authToken(), gameId, playerType);
        webSocketFacade.joinPlayer(this.userToken.authToken(), gameId, playerType);
    }


    public void joinObserver(int gameId) throws ResponseException {
        webSocketFacade.joinObserver(this.userToken.authToken(),gameId);
    }

    public void redrawChessBoard() {
        // Assuming 'board' is an object that contains the current state of the chess board
        BoardBuilder.redrawChessBoard(); // This method should handle the drawing of the chess board
    }

    public void leaveGame(int gameId) throws Exception {
        this.webSocketFacade.leaveGame(this.userToken.authToken(), gameId);
        //transitionToPostLoginUI(); // Method to handle UI transition after leaving the game
    }

}
