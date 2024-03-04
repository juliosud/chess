package server;

import com.google.gson.Gson;
import dataAccess.AuthDao;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import model.UserData;
import model.AuthData;
import model.GameData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;
import java.util.Map;


public class Server {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        AuthDao authDao = new AuthDao();
        GameDao gameDao = new GameDao();
        UserDao userDao = new UserDao();

        this.userService = new UserService(userDao, authDao);
        this.authService = new AuthService(authDao);
        this.gameService = new GameService(gameDao,authDao);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.awaitInitialization();
        return Spark.port();
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
            return gson.toJson(Map.of("message", "Error: bad request"));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
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
            // Handling unauthorized access (wrong username/password)
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            // Handling internal server errors, such as database access issues
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
            res.status(200); // HTTP 200 OK
            return "Logout successful";
        } catch (UnauthorizedException e) {
            // Handling unauthorized access (wrong username/password)
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            // Handling internal server errors, such as database access issues
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
            GameData createdGame = gameService.createGame(authToken, gameData);
            res.status(200);
            return gson.toJson(createdGame);
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

    private Object listGames(Request req, Response res) throws DataAccessException{
        String authToken = req.headers("Authorization");
        List<GameData> games = gameService.listGames(authToken);
        res.status(200); // HTTP 200 OK
        res.type("application/json");
        return gson.toJson(Map.of("games", games));
    }

    private Object joinGame(Request req, Response res) throws DataAccessException{
        String authToken = req.headers("Authorization");
        GameData joinGameData = gson.fromJson(req.body(), GameData.class);
        gameService.joinGame(authToken, joinGameData);
        res.status(200); // HTTP 200 OK
        return "Join game successful";
    }

    private Object clear(Request req, Response res) {
        userService.clearAll();
        gameService.clearAll();
        res.status(200); // HTTP 200 OK
        return "All data cleared";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}