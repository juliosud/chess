package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
//        throw new RuntimeException("Not implemented");
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        Collection<ChessMove> moves = new ArrayList<>();

        switch (this.getPieceType()){
            case KING:
                addKingMoves(moves, board, myPosition);
                break;
            case QUEEN:
                addQueenMoves(moves, board, myPosition);
                break;
            case BISHOP:
                addBishopMoves(moves, board, myPosition);
                break;
            case KNIGHT:
                addKnightMoves(moves, board, myPosition);
                break;
            case ROOK:
                addRookMoves(moves, board, myPosition);
                break;
            case PAWN:
                addPawnMoves(moves, board, myPosition);
                break;
        }

        return moves;
    }

    private void addPawnMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int direction = this.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startRow = this.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promotion = this.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        addForwardMoves(moves, board, myPosition, direction, startRow, promotion);
        addCaptureMoves(moves, board, myPosition, direction, startRow, promotion);
    }

    private void addCaptureMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int direction, int startRow, int promotion) {
        int forwardRow = myPosition.getRow() + direction;
        int[] captureCols = {myPosition.getColumn()-1, myPosition.getColumn()+1};

        for (int col : captureCols){
            if (isValidMove(forwardRow, col)){
                ChessPosition newPosition = new ChessPosition(forwardRow, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                //valid
                if (pieceAtDestination != null && pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                    //promotion
                    if (forwardRow == promotion) {
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
    }

    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int direction, int startRow, int promotion) {
        int forwardRow = myPosition.getRow() + direction;
        int col = myPosition.getColumn();

        ChessPosition newPosition = new ChessPosition(forwardRow, col);
        ChessPiece pieceAtDestination = board.getPiece(newPosition);

        //one move
        if (isValidMove(forwardRow, col) && pieceAtDestination == null){
            //check for promotion
            if (forwardRow == promotion){
                moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }

            //two moves
            if (myPosition.getRow() == startRow){
                int ForwardRowTwo = myPosition.getRow() + 2 * direction;

                ChessPosition newPositionTwo = new ChessPosition(ForwardRowTwo, col);
                ChessPiece pieceAtDestinationTwo = board.getPiece(newPositionTwo);

                if (pieceAtDestinationTwo == null){
                    moves.add(new ChessMove(myPosition, newPositionTwo, null));
                }
            }
        }
    }


    private void addRookMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        //moves
        int[][] rookMoves = {
                {1,0},{-1,0},
                {0,1},{0,-1}
        };

        for (int[] move : rookMoves){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true){
                row += move[0];
                col += move[1];

                if (!isValidMove(row, col)){
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }else {
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addKnightMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        //moves
        int[][] knightMoves = {
                {1,2},{1,-2},
                {-1,2},{-1,-2},
                {2,1},{2,-1},
                {-2,1},{-2,-1}
        };

        for (int[] move : knightMoves){
            int row = myPosition.getRow() + move[0];
            int col = myPosition.getColumn() + move[1];

            if (isValidMove(row, col)){
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null || pieceAtDestination.getTeamColor() != this.getTeamColor()){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void addBishopMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        //Bishop moves
        int[][] bishopMoves = {
                {1,1},{1,-1},
                {-1,1},{-1,-1}
        };

        for (int[] move : bishopMoves){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true){
                row += move[0];
                col += move[1];

                if (!isValidMove(row, col)){
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addQueenMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int[][] queenMoves = {
                {1,0},{-1,0},
                {0,1},{0,-1},
                {1,1},{1,-1},
                {-1,1},{-1,-1}
        };

        for (int[] move : queenMoves){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true){
                row += move[0];
                col += move[1];

                if (!isValidMove(row, col)){
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addKingMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        //KingMoves
        int[][] kingMoves = {
                {1,0},{-1,0},
                {0,1},{0,-1},
                {1,1},{1,-1},
                {-1,1},{-1,-1}
        };

        for (int[] move : kingMoves)
        {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];

            if (isValidMove(newRow, newCol)){
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null || pieceAtDestination.getTeamColor() != this.getTeamColor()){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private boolean isValidMove(int newRow, int newCol) {
        return newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
