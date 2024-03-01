package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDao implements IAuthDao {
    private Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void insertAuthToken(AuthData authToken) {
        tokens.put(authToken.authToken(), authToken);
    }

    @Override
    public AuthData getAuthToken(String authToken) {
        return tokens.get(authToken);
    }

    @Override
    public void deleteAuthToken(String authToken) {
        tokens.remove(authToken);
    }

    @Override
    public void clear(){
        tokens.clear();
    }
}
