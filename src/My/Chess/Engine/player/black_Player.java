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

public class black_Player extends Player{
    public black_Player(final Board board, final Collection<Move> white_legal_moves, final Collection<Move> black_legal_moves) {
        super(board, black_legal_moves, white_legal_moves);
    }

    @Override
    public Collection<Piece> get_active_pieces() {
        return this.board.get_black_pieces();
    }

    @Override
    public Colour get_colour() {
        return Colour.BLACK;
    }

    @Override
    public Player get_opponent() {
        return this.board.white_player();
    }

    @Override
    protected Collection<Move> calculate_king_castles(final Collection<Move> player_legals, final Collection<Move> opponent_legals) {
        final List<Move> king_castles = new ArrayList<>();

        if (this.player_king.is_first_move() && !is_in_check()) {
            //Black's king side castle
            if (!this.board.get_tile(5).is_tile_used() && !this.board.get_tile(6).is_tile_used()) {
                final Tile rook_tile = this.board.get_tile(7);
                if (rook_tile.is_tile_used() && rook_tile.get_piece().is_first_move()) {
                    if(Player.calculate_attacks_on_tile(5, opponent_legals).isEmpty() &&
                            Player.calculate_attacks_on_tile(6, opponent_legals).isEmpty() &&
                            rook_tile.get_piece().get_piece_type().is_rook()) {

                        king_castles.add(new Move.king_side_castle(this.board, this.player_king, 6,
                                (Rook) rook_tile.get_piece(), rook_tile.get_tile_coordinate(), 5));
                    }
                }
            }
            //Black's queen side castle add more condition
            if (!this.board.get_tile(1).is_tile_used() &&
                    !this.board.get_tile(2).is_tile_used() &&
                    !this.board.get_tile(3).is_tile_used()){

                final Tile rook_tile = this.board.get_tile(0);
                if(rook_tile.is_tile_used() && rook_tile.get_piece().is_first_move() &&
                Player.calculate_attacks_on_tile(2, opponent_legals).isEmpty() &&
                Player.calculate_attacks_on_tile(3, opponent_legals).isEmpty() &&
                rook_tile.get_piece().get_piece_type().is_rook()){

                    king_castles.add(new Move.queen_side_castle(this.board, this.player_king, 2,
                            (Rook) rook_tile.get_piece(), rook_tile.get_tile_coordinate(), 3));
                }
            }
        }

        return Collections.unmodifiableList(king_castles);
    }
}

