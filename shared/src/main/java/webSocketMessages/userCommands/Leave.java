package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    public final Integer gameID;

    public Leave(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}