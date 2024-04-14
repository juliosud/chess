package ui;

import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
public interface NotificationHandler {
    void notify(Notification notification);

    void load(LoadGame loadGame);

    void warn(Error error);

}
