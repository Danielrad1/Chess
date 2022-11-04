package My.Chess.Engine.player.ai;

import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.player.Move_Transition;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class MoveOrdering {

    private final Standard_Board_Evaluator evaluator;

    private static final MoveOrdering INSTANCE = new MoveOrdering();
    private static final int ORDER_SEARCH_DEPTH = 1;

    private MoveOrdering() {
        this.evaluator = new Standard_Board_Evaluator();
    }

    public static MoveOrdering get() {
        return INSTANCE;
    }

    public List<Move> orderMoves(final Board board) {
        return go(board, ORDER_SEARCH_DEPTH);
    }

    private static class MoveOrderEntry {
        final Move move;
        final int score;

        MoveOrderEntry(final Move move, final int score) {
            this.move = move;
            this.score = score;
        }

        final Move getMove() {
            return this.move;
        }

        final int getScore() {
            return this.score;
        }

        @Override
        public String toString() {
            return "move = " +this.move+ " score = " +this.score;
        }
    }

    private List<Move> go(final Board board,
                          final int depth) {
        final List<MoveOrderEntry> moveOrderEntries = new ArrayList<>();
        final boolean SORT_DESCENDING = board.current_player().get_colour().is_white();
        for (final Move move : board.current_player().get_legal_moves()) {
            final Move_Transition moveTransition = board.current_player().make_move(move);
            if (moveTransition.Get_move_status().is_done()) {
                final int current_value = board.current_player().get_colour().is_white() ?
                        min(moveTransition.get_transition_board(), depth - 1) :
                        max(moveTransition.get_transition_board(), depth - 1);
                moveOrderEntries.add(new MoveOrderEntry(move, current_value));
            }
        }

        if (SORT_DESCENDING) {
            Collections.sort(moveOrderEntries, new Comparator<MoveOrderEntry>() {
                @Override
                public int compare(final MoveOrderEntry o1,
                                   final MoveOrderEntry o2) {
                    return Ints.compare(o2.getScore(), o1.getScore());
                }
            });
        } else {
            Collections.sort(moveOrderEntries, new Comparator<MoveOrderEntry>() {
                @Override
                public int compare(final MoveOrderEntry o1,
                                   final MoveOrderEntry o2) {
                    return Ints.compare(o1.getScore(), o2.getScore());
                }
            });
        }

        final List<Move> orderedMoves = new ArrayList<>();
        for(MoveOrderEntry entry : moveOrderEntries) {
            orderedMoves.add(entry.getMove());
        }

        return ImmutableList.copyOf(orderedMoves);
    }

    public int min(final Board board,
                   final int depth) {
        if(depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int lowest_seen_value = Integer.MAX_VALUE;
        for (final Move move : board.current_player().get_legal_moves()) {
            final Move_Transition moveTransition = board.current_player().make_move(move);
            if (moveTransition.Get_move_status().is_done()) {
                final int current_value = max(moveTransition.get_transition_board(), depth - 1);
                if (current_value <= lowest_seen_value) {
                    lowest_seen_value = current_value;
                }
            }
        }
        return lowest_seen_value;
    }

    public int max(final Board board,
                   final int depth) {
        if(depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int highest_seen_value = Integer.MIN_VALUE;
        for (final Move move : board.current_player().get_legal_moves()) {
            final Move_Transition moveTransition = board.current_player().make_move(move);
            if (moveTransition.Get_move_status().is_done()) {
                final int current_value = min(moveTransition.get_transition_board(), depth - 1);
                if (current_value >= highest_seen_value) {
                    highest_seen_value = current_value;
                }
            }
        }
        return highest_seen_value;
    }

    private static boolean isEndGameScenario(final Board board) {
        return  board.current_player().is_in_checkmate() ||
                board.current_player().is_in_stalemate() ||
                board.current_player().get_opponent().is_in_checkmate() ||
                board.current_player().get_opponent().is_in_stalemate();
    }

}