package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        //throw new RuntimeException("Not implemented");
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        //throw new RuntimeException("Not implemented");
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //throw new RuntimeException("Not implemented");
        ChessPiece piece = board.getPiece(startPosition);


        if (piece == null){
            return null;
        }

//        Collection<ChessMove> moves = new ArrayList<>(piece.pieceMoves(board, startPosition));
//        Iterator<ChessMove> iterator = moves.iterator();
//
//        while (iterator.hasNext()) {
//            ChessMove move = iterator.next();
//            ChessBoard testBoard = board.deepCopy();
//            ChessGame testGame = new ChessGame(); // Assume constructor that accepts a board and teamTurn
//            testGame.board = testBoard;
//            testGame.teamTurn = teamTurn;
//
//            try {
//                testGame.makeMove(move);
//                if (testGame.isInCheck(piece.getTeamColor())) {
//                    iterator.remove(); // Remove the move if it results in a check
//                }
//            } catch (InvalidMoveException e) {
//                iterator.remove(); // Remove the move if it's invalid
//            }
//        }

        Collection<ChessMove> moves = new ArrayList<>(piece.pieceMoves(board, startPosition));
        Iterator<ChessMove> iterator = moves.iterator();

        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            if (simulateMoveAndCheck(move,piece.getTeamColor())){
                iterator.remove();
            }
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //throw new RuntimeException("Not implemented");;
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("No piece at the start position or not your turn.");
        }

//        Collection<ChessMove> moves = piece.pieceMoves(board, move.getStartPosition());
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        }

        //if all exceptions arent thrown, the execute the move
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

        // Check for pawn promotion
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
        }

        // Switch turns
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //throw new RuntimeException("Not implemented");
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null){
            return false; // King is not found
        }

        for (int row = 0 ; row < 8 ; row++){
            for (int col = 0 ; col < 8 ; col++){
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves){
                        if (move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //throw new RuntimeException("Not implemented");
        if (!isInCheck(teamColor)) {
            return false; // In check, so cannot be stalemate
        }
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (!simulateMoveAndCheck(move, teamColor)) {
                            return false; // Found a legal move, not stalemate
                        }
                    }
                }
            }
        }
        return true; // No legal moves available
    }

    // This method is conceptual and needs to be integrated with your actual game logic
    public boolean simulateMoveAndCheck(ChessMove move, TeamColor teamColor) {
        // Save the original state
        ChessPiece originalPieceAtEnd = board.getPiece(move.getEndPosition());
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());

        // Perform the move
        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);

        // Check for check status
        boolean isInCheckAfterMove = isInCheck(teamColor); // You need to implement this based on your game logic

        // Revert the move to original state
        board.addPiece(move.getStartPosition(), movingPiece);
        board.addPiece(move.getEndPosition(), originalPieceAtEnd);

        return isInCheckAfterMove;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false; // Can't be stalemate if in check
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (!simulateMoveAndCheck(move, teamColor)) {
                            return false; // Found at least one valid move, so not stalemate
                        }
                    }
                }
            }
        }
        return true; // No valid moves found, so it's a stalemate
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
//        throw new RuntimeException("Not implemented");
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
//        throw new RuntimeException("Not implemented");
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}