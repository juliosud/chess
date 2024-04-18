package server;


import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.MySqlAuthDao;
import dataAccess.MySqlGameDao;
import dataAccess.MySqlUserDao;
import dataAccess.exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    MySqlAuthDao authDao = new MySqlAuthDao();
    MySqlGameDao gameDao = new MySqlGameDao();
    MySqlUserDao userDao = new MySqlUserDao();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayer.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserver.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class), session);
            case FETCH_GAME_STATE -> onFetchGameState(new Gson().fromJson(message, FetchGameState.class), session);
        }
    }

    private void onFetchGameState(FetchGameState command, Session session) throws DataAccessException {
        ChessGame gameState = gameDao.getGameState(command.gameID);
        Gson gson = new Gson();
        try {
            session.getRemote().sendString(gson.toJson(new LoadGame(gameState)));
        } catch (IOException e) {

        }
    }

    private void resign(Resign resign, Session session) {
        String authToken = resign.getAuthString();
        int gameId = resign.gameID;

        try {
            AuthData user = authDao.getAuthToken(authToken);
            if (user != null) {
                GameData game = gameDao.getGame(gameId);
                if (game != null) {
                    // Check if the user is white or black and update accordingly
                    if (user.username().equals(game.whiteUsername())) {
                        GameData newGame = new GameData(gameId, null, game.blackUsername(), game.gameName());
                        gameDao.updateGame(newGame);
                    } else if (user.username().equals(game.blackUsername())) {
                        GameData newGame = new GameData(gameId, game.whiteUsername(), null, game.gameName());
                        gameDao.updateGame(newGame);
                    }
                    ChessGame gameState = gameDao.getGameState(game.gameID());
                    gameState.terminateGame();

                    // Notify players about the resignation
                    Notification notification = new Notification(user.username() + " has resigned.");
                    connections.broadcast(authToken, gameId, notification);

                    // Remove the resigning player from the connection manager
                    connections.remove(authToken);
                } else {
                    session.getRemote().sendString(new Gson().toJson(new Error("Failed to resign or game not found.")));
                }
            } else {
                session.getRemote().sendString(new Gson().toJson(new Error("Unauthorized access.")));
            }
        } catch (DataAccessException | IOException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }



    private void leave(Leave leave, Session session) throws IOException {
        String authToken = leave.getAuthString();
        int gameId = leave.gameID;
        try {
            AuthData user = authDao.getAuthToken(authToken);
            if (user != null) {
                GameData game = gameDao.getGame(gameId);
                if (game != null) {
                    // Check if the user is white or black and update accordingly
                    if (user.username().equals(game.whiteUsername())) {
                        GameData newGame = new GameData(gameId, null, game.blackUsername(), game.gameName());
                        gameDao.updateGame(newGame);
                    } else if (user.username().equals(game.blackUsername())) {
                        GameData newGame = new GameData(gameId, game.whiteUsername(), null, game.gameName());
                        gameDao.updateGame(newGame);
                    }
                    connections.remove(authToken);
                    Notification notification = new Notification(user.username() + " has left the game.");
                    connections.broadcast(authToken, gameId, notification);
                } else {
                    session.getRemote().sendString(new Gson().toJson(new Error("Failed to leave game or game not found.")));
                }
            } else {
                session.getRemote().sendString(new Gson().toJson(new Error("Unauthorized access.")));
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Error accessing data: " + e.getMessage())));
        }
    }



    private void makeMove(MakeMove command, Session session) throws DataAccessException {
        String authToken = command.getAuthString();
        int gameId = command.gameID;

        try {
            AuthData user = authDao.getAuthToken(authToken);
            if (user != null) {
                GameData game = gameDao.getGame(gameId);
                if (game != null) {
                    ChessGame gameToMove = gameDao.getGameState(gameId);
                    gameToMove.makeMove(command.move);
                    gameDao.updateGameState(gameId,gameToMove);
                    Notification notification = new Notification(user.username() + " made a move: " + command.move);
                    connections.broadcast(authToken, gameId, notification);

                }
            }
        } catch (InvalidMoveException | IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void joinObserver(JoinObserver joinObserver, Session session) throws DataAccessException, IOException {
        String authToken = joinObserver.getAuthString();
        int gameId = joinObserver.gameID;
        AuthData user = authDao.getAuthToken(authToken);
        String userName = user.username();

        if (authDao.getAuthToken(authToken) != null) {
            if (gameDao.getGame(gameId) != null) {
                connections.add(authToken, gameId, session);
                Notification notification = new Notification("join," + userName + " joined the game as OBSERVER.");
                connections.broadcast(authToken, gameId, notification);
            } else {
                session.getRemote().sendString("Failed to join game.");
            }
        } else {
            session.getRemote().sendString("Unauthorized access.");
        }
    }

    private void joinPlayer(JoinPlayer joinPlayer, Session session) throws IOException, DataAccessException {
        String authToken = joinPlayer.getAuthString();
        int gameId = joinPlayer.gameID;
        AuthData user = authDao.getAuthToken(authToken);
        String userName = user.username();

        if (authDao.getAuthToken(authToken) != null) {
            if (gameDao.getGame(gameId) != null) {
                connections.add(authToken, gameId, session);
                Notification notification = new Notification("join," + userName + " joined the game.");
                connections.broadcast(authToken, gameId, notification);
            } else {
                session.getRemote().sendString("Failed to join game.");
            }
        } else {
            session.getRemote().sendString("Unauthorized access.");
        }
    }
}
