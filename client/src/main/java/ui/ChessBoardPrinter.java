package ui;


import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;
import chess.ChessBoard;

public class ChessBoardPrinter {

    public static void printGenericBoard() {
        System.out.println("Printing a generic chessboard:");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }
                System.out.print(EscapeSequences.EMPTY);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printChessBoard(ChessBoard board) {
        System.out.println("Printing the given ChessBoard:");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null) {
                    if (piece.pieceColor == ChessGame.TeamColor.WHITE) {
                        System.out.print(getPieceRepresentation(piece.type, true));
                    } else {
                        System.out.print(getPieceRepresentation(piece.type, false));
                    }
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static String getPieceRepresentation(ChessPiece.PieceType type, boolean isWhite) {
        switch (type) {
            case KING:
                return isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP:
                return isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK:
                return isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN:
                return isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }
}
