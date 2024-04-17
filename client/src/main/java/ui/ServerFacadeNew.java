package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.GameData;
import ui.ResponseException;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacadeNew {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacadeNew(String url) {
        this.serverUrl = url;
    }

    public AuthData register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequestWithAuth("DELETE", path, null, null, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequestWithAuth("GET", path, null, GameData[].class, authToken);
    }

    public Map<String, Integer> createGame(String authToken, GameData game) throws ResponseException {
        var path = "/game";
        return this.makeRequestWithAuth("POST", path, game, Map.class, authToken);
    }

    public void joinGame(String authToken, Integer gameId, String playerType) throws ResponseException {
        var path = "/game";
        Map<String, Object> requestBody = Map.of("gameID", gameId, "playerColor", playerType);
        this.makeRequestWithAuth("PUT", path, requestBody, null, authToken);
    }

    public void clearData() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequestWithAuth(method, path, request, responseClass, null);
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (request != null) {
                http.setDoOutput(true);
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        http.addRequestProperty("Content-Type", "application/json");
        String reqData = new Gson().toJson(request);
        try (OutputStream reqBody = http.getOutputStream()) {
            reqBody.write(reqData.getBytes());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new ResponseException(status, "HTTP Error: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass != null) {
            try (InputStream respBody = (http.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) ? http.getInputStream() : http.getErrorStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                return new Gson().fromJson(reader, responseClass);
            }
        }
        return null;
    }
}
