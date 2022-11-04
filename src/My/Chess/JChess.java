package My.Chess;

import My.Chess.Engine.board.Board;
import My.Chess.gui.Table;

public class JChess {

    public static void main(String[] args) {

        Board board = Board.create_starting_position();

        Table.get().show();
    }
}
