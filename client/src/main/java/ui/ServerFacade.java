package ui;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Collection;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public AuthData register(UserData newUser) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newUser)))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Error occurred: " + response.body());
        }
    }

    public AuthData login(UserData loginUser) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(loginUser)))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Login error: " + response.body());
        }
    }

    public void logout(String authToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .DELETE()
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Logout error: " + response.body());
        }
    }


    public Collection<GameData> listGames(String authToken) throws Exception {
        return null;
    }

    public GameData createGame(String authToken, GameData newGame) throws Exception {
        return newGame;
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws Exception {
        // Implementation for joining a game
    }

}
