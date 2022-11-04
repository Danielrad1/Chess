package My.Chess.Engine.player;

import My.Chess.Engine.Colour;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.board.Tile;
import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.Pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class white_Player extends Player {
    public white_Player(final Board board, final Collection<Move> white_legal_moves, final Collection<Move> black_legal_moves) {
        super(board, white_legal_moves, black_legal_moves);
    }

    @Override
    public Collection<Piece> get_active_pieces() {
        return this.board.get_white_pieces();
    }

    @Override
    public Colour get_colour() {
        return Colour.WHITE;
    }

    @Override
    public Player get_opponent() {
        return this.board.black_player();
    }

    @Override
    protected Collection<Move> calculate_king_castles(final Collection<Move> player_legals, final Collection<Move> opponent_legals) {
        final List<Move> king_castles = new ArrayList<>();

        if (this.player_king.is_first_move() && !is_in_check()) {
            //White's king side castle
            if (!this.board.get_tile(61).is_tile_used() && !this.board.get_tile(62).is_tile_used()) {
                final Tile rook_tile = this.board.get_tile(63);
                if (rook_tile.is_tile_used() && rook_tile.get_piece().is_first_move()) {
                    if(calculate_attacks_on_tile(61, opponent_legals).isEmpty() &&
                       calculate_attacks_on_tile(62, opponent_legals).isEmpty() &&
                        rook_tile.get_piece().get_piece_type().is_rook()) {

                        king_castles.add(new Move.king_side_castle(this.board, this.player_king, 62,
                                (Rook) rook_tile.get_piece(), rook_tile.get_tile_coordinate(), 61));
                    }
                }
            }
            //White's queen side castle ADD CHECK CONFITION
            if (!this.board.get_tile(59).is_tile_used() &&
                    !this.board.get_tile(58).is_tile_used() &&
                    !this.board.get_tile(57).is_tile_used()){

                final Tile rook_tile = this.board.get_tile(56);
                if(rook_tile.is_tile_used() && rook_tile.get_piece().is_first_move() &&
                        Player.calculate_attacks_on_tile(58, opponent_legals).isEmpty() &&
                        Player.calculate_attacks_on_tile(59, opponent_legals).isEmpty() &&
                        rook_tile.get_piece().get_piece_type().is_rook()){
                    king_castles.add(new Move.queen_side_castle(this.board, this.player_king, 58,
                            (Rook) rook_tile.get_piece(), rook_tile.get_tile_coordinate(), 59));
                }
            }
        }


        return Collections.unmodifiableList(king_castles);
    }

}
