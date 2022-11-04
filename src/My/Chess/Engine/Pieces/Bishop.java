package My.Chess.Engine.Pieces;

import My.Chess.Engine.Colour;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.BoardUtils;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Bishop extends Piece {

    private final static int[] candidate_move_vectors = {-9, -7, 7, 9};

    public Bishop(final Colour colour, final int position) {
        super(Piece_Type.BISHOP, position, colour, true);
    }

    public Bishop(final Colour colour, final int position, final boolean is_first_move) {
        super(Piece_Type.BISHOP, position, colour, is_first_move);
    }

    @Override
    public Collection<Move> calculate_legal_moves(final Board board) {

        final List<Move> legal_moves = new ArrayList<>();

        for (final int offset : candidate_move_vectors){
            int candidate_destination_coordinate = this.piece_position;

            while(BoardUtils.is_valid_tile_coordinate(candidate_destination_coordinate)){

                if (is_first_column_exception(candidate_destination_coordinate, offset)
                || is_eighth_column_exception(candidate_destination_coordinate, offset)){
                    break;
                }

                candidate_destination_coordinate += offset;

                if (BoardUtils.is_valid_tile_coordinate(candidate_destination_coordinate)){

                    final Tile candidate_destination_tile = board.get_tile(candidate_destination_coordinate);

                    if (!candidate_destination_tile.is_tile_used()) {
                        legal_moves.add(new Move.major_move(board, this, candidate_destination_coordinate));
                    }
                    else {
                        final Piece piece_at_destination = candidate_destination_tile.get_piece();
                        final Colour colour_at_destination = piece_at_destination.get_piece_colour();

                        if (this.piece_colour != colour_at_destination) {
                            legal_moves.add(new Move.major_attack_move(board, this,
                                    candidate_destination_coordinate, piece_at_destination));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legal_moves);
    }

    @Override
    public Bishop Move_piece(Move move) {
        return new Bishop(move.get_moved_piece().get_piece_colour(), move.Get_Destination());
    }

    @Override
    public int locationBonus() {
        return this.piece_colour.bishopBonus(this.piece_position);
    }

    @Override
    public String toString() {
        return Piece_Type.BISHOP.toString();
    }

    public static boolean is_first_column_exception(final int position, final int offset){
        return BoardUtils.is_a_file(position) && (offset == -9 || offset == 7);
    }
    public static boolean is_eighth_column_exception(final int position, final int offset){
        return BoardUtils.is_h_file(position) && (offset == 9 || offset == -7);
    }

}
