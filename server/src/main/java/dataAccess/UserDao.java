package dataAccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDao implements IUserDao {
    private Map<String, UserData> users = new HashMap<>();

    @Override
    public void insertUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
