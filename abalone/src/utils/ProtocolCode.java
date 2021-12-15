package utils;

public class ProtocolCode {

    //client to server command
    final public static String CREATE_COMMAND = "CREATE";
    final public static String JOIN_COMMAND = "JOIN";
    final public static String READY_COMMAND = "READY";
    final public static String MOVE_COMMAND = "MOVE";
    final public static String EXIT_COMMAND = "EXIT";
    final public static String RECONNECT_COMMAND = "RECONNECT";

    //server to client command
    final public static String ERROR_COMMAND = "ERROR";
    final public static String LOBBY_COMMAND = "LOBBY";
    final public static String START_COMMAND = "START";
    final public static String WARNING_COMMAND = "WARNING";
    final public static String RANDOM_COMMAND = "RANDOM";
    final public static String UPDATE_COMMAND = "UPDATE";
    final public static String SCORE_COMMAND = "SCORE";
    final public static String DISCONNECTED_COMMAND = "DISCONNECTED";

    //ERROR message
    final public static String UNKNOWN_ERR = "Generic / unknown error.";
    final public static String UNRECOGNIZECOMMAND_ERR = "Unrecognized command.";
    final public static String INVALIDARGU_ERR = "Invalid arguments.";
    final public static String INVALIDNAME_ERR = "Invalid player name.";
    final public static String NAMEDEPULICATE_ERR = "Player name already taken.";
    final public static String NOTTURN_ERR = "Not your turn.";
    final public static String INVALIDMOVE_ERR = "Invalid move.";
    final public static String NOTLOBBY_ERR = "Not in a lobby.";
    final public static String NOTGAME_ERR = "Not in a game.";
    final public static String UNSUPPORTED_ERR = "Unsupported extension";


}
