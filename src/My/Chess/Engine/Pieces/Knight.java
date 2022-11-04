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


public class Knight extends Piece {

    private  final static int[] candidate_move_coordinates = {6, 10, 15, 17, -6, -10, -15, -17};

    public Knight(final Colour colour, final int position) {super(Piece_Type.KNIGHT,position, colour, true);}
    public Knight(final Colour colour, final int position, final boolean is_first_move) {
        super(Piece_Type.KNIGHT, position, colour, is_first_move);
    }

    @Override
    public Collection<Move> calculate_legal_moves(final Board board) {

        final List<Move> legal_moves = new ArrayList<>();

        for (final int candidate : candidate_move_coordinates){

            final int candidate_destination_coordinate = this.piece_position + candidate;

            if (BoardUtils.is_valid_tile_coordinate(candidate_destination_coordinate)) {
                if (is_first_column_exception(this.piece_position, candidate) ||
                        is_second_column_exception(this.piece_position, candidate) ||
                        is_seventh_column_exception(this.piece_position, candidate) ||
                        is_eighth_column_exception(this.piece_position, candidate)){
                    continue;
                }


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
                }
            }
        }
        return Collections.unmodifiableList(legal_moves);
    }

    @Override
    public Knight Move_piece(Move move) {
        return new Knight(move.get_moved_piece().get_piece_colour(), move.Get_Destination());
    }

    @Override
    public int locationBonus() {
        return this.piece_colour.knightBonus(this.piece_position);
    }

    @Override
    public String toString() {
        return Piece_Type.KNIGHT.toString();
    }

    // NOTE DIFFERENT FROM GUY BUT MAY BE SLOWER? WATCH VID 5
    private static boolean is_first_column_exception(final int position, final int offset){
        return BoardUtils.is_a_file(position) && (offset == -17 || offset == -10
                || offset == 6 || offset == 15);
    }
    private static boolean is_second_column_exception(final int position, final int offset){
        return BoardUtils.is_b_file(position) && (offset == -10 || offset == 6);
    }

    private static boolean is_seventh_column_exception(final int position, final int offset){
        return BoardUtils.is_f_file(position) && (offset == 10 || offset == -6);
    }
    private static boolean is_eighth_column_exception(final int position, final int offset){
        return BoardUtils.is_h_file(position) && (offset == 17 || offset == 10
                || offset == -6 || offset == -15);
    }


}
