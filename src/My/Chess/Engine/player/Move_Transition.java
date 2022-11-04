package My.Chess.Engine.player;

import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;

public class Move_Transition {
    private final Board transition_board;
    private final Move move;
    private final Move_Status move_status;

    public Move_Transition(final Board transition_board, final Move move, final Move_Status status){
        this.transition_board = transition_board;
        this.move = move;
        this.move_status = status;

    }

    public Move_Status Get_move_status(){return this.move_status;}

    public Board get_transition_board() {
        return this.transition_board;
    }
}
