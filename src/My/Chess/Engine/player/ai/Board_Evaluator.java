package My.Chess.Engine.player.ai;

import My.Chess.Engine.board.Board;

public interface Board_Evaluator {
    int evaluate(Board board, int depth);
}
