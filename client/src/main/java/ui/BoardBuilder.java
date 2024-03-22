package ui;

public class BoardBuilder {

    private static final int BOARD_SIZE = 8;
    private static final int CELL_WIDTH = 4;
    private static final String[][] pieces = new String[BOARD_SIZE][BOARD_SIZE];

    public BoardBuilder() {
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize the board with pawns
        for (int i = 0; i < BOARD_SIZE; i++) {
            pieces[1][i] = EscapeSequences.WHITE_PAWN;
            pieces[6][i] = EscapeSequences.BLACK_PAWN;
        }

        // Place other pieces
        pieces[0][0] = pieces[0][7] = EscapeSequences.WHITE_ROOK;
        pieces[0][1] = pieces[0][6] = EscapeSequences.WHITE_KNIGHT;
        pieces[0][2] = pieces[0][5] = EscapeSequences.WHITE_BISHOP;
        pieces[0][3] = EscapeSequences.WHITE_QUEEN;
        pieces[0][4] = EscapeSequences.WHITE_KING;

        pieces[7][0] = pieces[7][7] = EscapeSequences.BLACK_ROOK;
        pieces[7][1] = pieces[7][6] = EscapeSequences.BLACK_KNIGHT;
        pieces[7][2] = pieces[7][5] = EscapeSequences.BLACK_BISHOP;
        pieces[7][3] = EscapeSequences.BLACK_QUEEN;
        pieces[7][4] = EscapeSequences.BLACK_KING;
    }

    public void printBoard() {
        printBoardInternal(false); // Print the board in standard orientation
        System.out.println();     // Add a space between boards
        printBoardInternal(true);  // Print the board in reversed orientation
    }

    private void printBoardInternal(boolean reverse) {
        if (!reverse) {
            printCoordinates(false);
        } else {
            printCoordinates(true);
        }

        for (int row = 1; row <= BOARD_SIZE; row++) {
            int displayRow = reverse ? row : BOARD_SIZE - row + 1;
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(displayRow + " ");

            for (int col = 1; col <= BOARD_SIZE; col++) {
                int displayCol = reverse ? BOARD_SIZE - col + 1 : col;
                System.out.print(EscapeSequences.moveCursorToLocation(displayCol * CELL_WIDTH, (reverse ? BOARD_SIZE - row + 2 : row + 1)));

                boolean isWhiteSquare = (row + col) % 2 == 0;
                String backgroundColor = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                String textColor = isWhiteSquare ? EscapeSequences.SET_TEXT_COLOR_BLACK : EscapeSequences.SET_TEXT_COLOR_WHITE;

                System.out.print(backgroundColor + textColor);

                String piece = pieces[BOARD_SIZE - displayRow][displayCol - 1];
                System.out.print(piece != null ? piece : EscapeSequences.EMPTY);

                System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }

            System.out.print(" " + displayRow);
            System.out.println();
        }

        if (!reverse) {
            printCoordinates(false);
        } else {
            printCoordinates(true);
        }
    }

    private void printCoordinates(boolean reverse) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "  ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + "  ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        BoardBuilder chessBoard = new BoardBuilder();
        chessBoard.printBoard();
    }
}
