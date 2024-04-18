package webSocketMessages.userCommands;

public class FetchGameState extends UserGameCommand{
    public Integer gameID;

    public FetchGameState(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.FETCH_GAME_STATE;
    }
}
