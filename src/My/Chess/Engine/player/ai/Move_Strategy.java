package My.Chess.Engine.player.ai;

import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;

public interface Move_Strategy {

    Move execute(Board board);
}
