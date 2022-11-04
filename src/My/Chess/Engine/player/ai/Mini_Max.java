package My.Chess.Engine.player.ai;

import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.player.Move_Transition;

public class Mini_Max implements Move_Strategy{
    private final Board_Evaluator board_evaluator;
    private final int search_depth;

    public Mini_Max(final int search_depth){
        this.board_evaluator = new Standard_Board_Evaluator();
        this.search_depth = search_depth;

    }

    @Override
    public String toString() {
        return "MINMAX";
    }

    @Override
    public Move execute(Board board) {
        final long start_time = System.currentTimeMillis();
        Move best_move = null;
        int highest_seen_value = Integer.MIN_VALUE;
        int lowest_seen_value = Integer.MAX_VALUE;
        int current_value;

        System.out.println(board.current_player() + " Thinking with depth " + this.search_depth);
        int num_moves = board.current_player().get_legal_moves().size();
        for (final Move move : board.current_player().get_legal_moves()){
            final Move_Transition move_transition = board.current_player().make_move(move);
            if (move_transition.Get_move_status().is_done()){
                current_value = board.current_player().get_colour().is_white() ?
                        min(move_transition.get_transition_board(), this.search_depth -1) :
                        max(move_transition.get_transition_board(), this.search_depth -1);

                if (board.current_player().get_colour().is_white() && current_value >= highest_seen_value){
                    highest_seen_value = current_value;
                    best_move = move;
                }
                else if (board.current_player().get_colour().is_black() && current_value <= lowest_seen_value){
                    lowest_seen_value = current_value;
                    best_move = move;
                }
            }
        }
        final float execution_time = System.currentTimeMillis() - start_time;
        System.out.printf("execution time: " + "%.2f" + "s", execution_time/1000);
        System.out.println("");
        if (board.current_player().get_colour().is_white()) System.out.println(highest_seen_value);
        else System.out.println(lowest_seen_value);
        return best_move;
    }
    private static boolean Game_Over(final Board board){
        return board.current_player().is_in_checkmate() || board.current_player().get_opponent().is_in_checkmate();
    }


    public int min(final Board board, final int depth){
        if (depth == 0 || Game_Over(board)) return this.board_evaluator.evaluate(board, depth);

        int lowest_seen_value = Integer.MAX_VALUE;
        for (final Move move : board.current_player().get_legal_moves()){
            final Move_Transition move_transition = board.current_player().make_move(move);
            if(move_transition.Get_move_status().is_done()){
                final int current_value = max(move_transition.get_transition_board(), depth -1);
                if (current_value <= lowest_seen_value) lowest_seen_value = current_value;
            }
        }
        return lowest_seen_value;
    }
    public int max(final Board board, final int depth){
        if (depth == 0 || Game_Over(board) ) return this.board_evaluator.evaluate(board, depth);

        int highest_seen_value = Integer.MIN_VALUE;
        for (final Move move : board.current_player().get_legal_moves()){
            final Move_Transition move_transition = board.current_player().make_move(move);
            if(move_transition.Get_move_status().is_done()){
                final int current_value = min(move_transition.get_transition_board(), depth -1);
                if (current_value >= highest_seen_value) highest_seen_value = current_value;
            }
        }
        return highest_seen_value;

    }
}
