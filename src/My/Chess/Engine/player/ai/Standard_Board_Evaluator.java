package My.Chess.Engine.player.ai;

import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.player.Player;

public final class Standard_Board_Evaluator implements Board_Evaluator {
    private static final int CHECK_BONUS = 20;
    private static final int QUEEN_ATTACK_BONUS = 40;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;
    private static final int CASTLE_CAPABLE_BONUS = 40;
    private static final int STALEMATE_PENALTY = -1000000000;
    private static final int MOBILITY_BONUS = 10; // multiplying factor


    @Override
    public int evaluate(final Board board, final int depth) {

        return Score_Player(board, board.white_player(), depth) - Score_Player(board, board.black_player(), depth);
    }

    private int Score_Player(final Board board, final Player player, final int depth) {

        return (
                CheckMate(player, depth) +
                stalemate(player) +
                Piece_Value(player) +
                Mobility(player) +
                queenattack(player)+
                Check(player) +
                castled(player)+
                castleCapable(player) +
                pieceValueAndLocationBonus(player) +
                pawnStructure(player)) ;
    }

    private int stalemate(Player player) {
        if (player.get_opponent().is_in_stalemate()) return STALEMATE_PENALTY;
        else return 0;
    }


    private static int pieceValueAndLocationBonus(final Player player) {
        int pieceValuationScore = 0;
        for (final Piece piece : player.get_active_pieces()) {
            pieceValuationScore += piece.get_piece_value() + piece.locationBonus();
        }
        return pieceValuationScore;
    }

    private static int castleCapable(final Player player) {
        return (player.is_King_side_castle_capable()|| player.is_Queen_side_castle_capable()) ? CASTLE_CAPABLE_BONUS : 0;
    }

    private static int castled(Player player) {
        return player.is_castled() ? CASTLE_BONUS : 0;
    }

    private int CheckMate(Player player, int depth ) {
        if(player.get_opponent().is_in_checkmate()) return  CHECKMATE_BONUS * depth_bonus(depth);
        else return 0;
    }

    private static int depth_bonus(int depth) {
        return depth == 0? 1 : DEPTH_BONUS * depth;
    }

    private static int Check(Player player) {
        return player.get_opponent().is_in_check() ? CHECK_BONUS :0;
    } 

    private static int Mobility(final Player player) {
        return player.get_legal_moves().size() * MOBILITY_BONUS;
    }
    private static int pawnStructure(final Player player) {
        return PawnStructureAnalyzer.get().pawnStructureScore(player);
    }
    public static int queenattack(Player player){
        return player.get_opponent().queen_is_in_check()? QUEEN_ATTACK_BONUS: 0;
    }

    private static int Piece_Value(final Player player) {
        int Piece_Value_Score = 0;
        for (final Piece piece : player.get_active_pieces()){
            Piece_Value_Score += piece.get_piece_value();
        }
        return Piece_Value_Score;
    }

}
