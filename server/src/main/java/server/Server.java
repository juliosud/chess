package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import model.UserData;
import model.AuthData;
import model.GameData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;
import java.util.Collection;
import java.util.Map;

import model.GameData;
import server.WebSocketHandler;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();
    private final WebSocketHandler webSocketHandler;

    public Server() {
        MySqlAuthDao authDao = new MySqlAuthDao();
        MySqlGameDao gameDao = new MySqlGameDao();
        MySqlUserDao userDao = new MySqlUserDao();

        this.userService = new UserService(userDao, authDao);
        this.gameService = new GameService(gameDao,authDao);
        this.webSocketHandler = new WebSocketHandler();

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", webSocketHandler);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.exception(Exception.class, this::exceptionHandler);
        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(Exception e, Request request, Response response) {
        response.status(500);
        response.body(gson.toJson(Map.of("message", "An error occurred: " + e.getMessage())));
    }

    private Object register(Request req, Response res) {
        res.type("application/json");
        try {
            UserData newUser = gson.fromJson(req.body(), UserData.class);
            AuthData authData = userService.register(newUser);
            res.status(200);
            return gson.toJson(authData);
        } catch (BadRequestException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: bad request."));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: already taken."));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: description"));
        }
    }

    private Object login(Request req, Response res) {
        res.type("application/json");
        try {
            UserData loginUser = gson.fromJson(req.body(), UserData.class);
            AuthData authData = userService.login(loginUser);
            res.status(200);
            return gson.toJson(authData);
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: description"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "An unexpected error occurred"));
        }
    }

    private Object logout(Request req, Response res) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            userService.logout(authToken);
            res.status(200);
            return "";
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: description"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "An unexpected error occurred"));
        }
    }

    private Object createGame(Request req, Response res) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            GameData gameData = gson.fromJson(req.body(), GameData.class);
            Integer createdGame = gameService.createGame(authToken, gameData);
            res.status(200);
            return gson.toJson(Map.of("gameID", createdGame));
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (BadRequestException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object listGames(Request req, Response res) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            Collection<GameData> games = gameService.listGames(authToken);
            res.status(200);
//            return gson.toJson(Map.of("games", games));
            return gson.toJson(games);
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: description"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "An unexpected error occurred"));
        }
    }

    private Object joinGame(Request req, Response res) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            String body = req.body();
            Map<String, Object> bodyMap = gson.fromJson(body, Map.class);
            GameData joinGameData = gson.fromJson(req.body(), GameData.class);
            int gameId = joinGameData.gameID();
            String playerColor = (String) bodyMap.get("playerColor");
            gameService.joinGame(authToken, gameId, playerColor);
            res.status(200);
            return "";

        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (BadRequestException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: bad request - " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "An unexpected error occurred - " + e.getMessage()));
        }
    }

    private Object clear(Request req, Response res) {
        res.type("application/json");
        try {
            userService.clear();
            gameService.clear();
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: description"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "An unexpected error occurred"));
        }
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}