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
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");
        return this.type;
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
//        return new ArrayList<>();
        Collection<ChessMove> moves = new ArrayList<>();

        switch (this.getPieceType()) {
            case KING:
                addKingMoves(moves, board, myPosition);
                break;
            case QUEEN:
                addQueenMoves(moves, board, myPosition);
                break;
            case BISHOP:
                addBishopMoves(moves, board, myPosition);
//                System.out.println(moves);
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
        int promotionRow = this.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        // Forward move
        int forwardRow = myPosition.getRow() + direction;
        if (isValidPosition(forwardRow, myPosition.getColumn()) && board.getPiece(new ChessPosition(forwardRow, myPosition.getColumn())) == null) {
            if (forwardRow == promotionRow) {
                // Pawn promotion logic - promoting to a queen for simplicity
                moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, myPosition.getColumn()), PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, myPosition.getColumn()), PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, myPosition.getColumn()), PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, myPosition.getColumn()), PieceType.BISHOP));

            } else {
                // Regular forward move
                moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, myPosition.getColumn()), null));
            }

            // Two-square move from starting position
            if (myPosition.getRow() == startRow) {
                int twoForwardRow = myPosition.getRow() + 2 * direction;
                if (isValidPosition(twoForwardRow, myPosition.getColumn()) && board.getPiece(new ChessPosition(twoForwardRow, myPosition.getColumn())) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(twoForwardRow, myPosition.getColumn()), null));
                }
            }
        }

        // Capture moves
        int[] captureCols = {myPosition.getColumn() - 1, myPosition.getColumn() + 1};
        for (int captureCol : captureCols) {
            if (isValidPosition(forwardRow, captureCol)) {
                ChessPiece capturePiece = board.getPiece(new ChessPosition(forwardRow, captureCol));
                if (capturePiece != null && capturePiece.getTeamColor() != this.getTeamColor()) {
                    if (forwardRow == promotionRow) {
                        // Capture with pawn promotion
                        moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, captureCol), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, captureCol), PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, captureCol), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, captureCol), PieceType.BISHOP));
                    } else {
                        // Regular capture
                        moves.add(new ChessMove(myPosition, new ChessPosition(forwardRow, captureCol), null));
                    }
                }
                // Include logic for En Passant capture here if applicable
            }
        }

        // Additional logic for special pawn moves like En Passant can be added here
    }


    private void addRookMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        // Check horizontal and vertical lines from the Rook's position
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (!isValidPosition(row, col)) {
                    break; // Break if the position is off the board
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null) {
                    // Add move if the square is empty
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    // Add move if the square contains an opponent's piece, but then break
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addKnightMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        // Possible 'L' shaped moves for a Knight
        int[][] knightMoves = {
                {2, 1}, {2, -1},
                {-2, 1}, {-2, -1},
                {1, 2}, {1, -2},
                {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];

            if (isValidPosition(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                // The Knight can move to the new position if it is empty or contains an opponent's piece
                if (pieceAtDestination == null || pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void addBishopMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            //This loop will iterate over all the diagonals until it is out of range
            while (true) {
                row += direction[0];
                col += direction[1];

                if (!isValidPosition(row, col)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                //check if the destination is null
                if (pieceAtDestination == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    // Add move if the square contains an opponent's piece, but then break
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addQueenMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 0}, {-1, 0},  // Vertical
                {0, 1}, {0, -1},  // Horizontal

                {1, 1}, {1, -1}, //Dia
                {-1, 1}, {-1, -1}//
        };

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (!isValidPosition(row, col)) {
                    break; // Break if the position is off the board
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                if (pieceAtDestination == null) {
                    // Add move if the square is empty
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    // Add move if the square contains an opponent's piece, but then break
                    if (pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addKingMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int[][] kingMoves = {
                {1, 0}, {-1, 0},  // Vertical moves
                {0, 1}, {0, -1},  // Horizontal moves
                {1, 1}, {1, -1},  // Diagonal moves
                {-1, 1}, {-1, -1}
        };

        for (int[] move : kingMoves) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];

            if (isValidPosition(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);

                // King can move to the new position if it's empty or contains an opponent's piece
                if (pieceAtDestination == null || pieceAtDestination.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessPiece that = (ChessPiece) obj;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }


}
