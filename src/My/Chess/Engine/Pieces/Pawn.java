package My.Chess.Engine.Pieces;

import My.Chess.Engine.Colour;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.BoardUtils;
import My.Chess.Engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] candidate_move_vectors = {8, 16, 7, 9};

    public Pawn(final Colour colour, final int position) {super(Piece_Type.PAWN,position, colour,true);}
    public Pawn(final Colour colour, final int position, final boolean is_first_move) {
        super(Piece_Type.PAWN, position, colour, is_first_move);
    }

    @Override
    public Collection<Move> calculate_legal_moves(final Board board) {

        final List<Move> legal_moves = new ArrayList<>();

        for (final int offset : candidate_move_vectors) {
            final int candidate_destination_coordinate = this.piece_position + (offset * (this.get_piece_colour().get_direction()));

            if (!BoardUtils.is_valid_tile_coordinate(candidate_destination_coordinate)) continue;


            if (offset == 8 && !board.get_tile(candidate_destination_coordinate).is_tile_used()) {
                if (this.piece_colour.is_pawn_promotion_square(candidate_destination_coordinate)){
                    legal_moves.add(new Move.pawn_promotion(new Move.pawn_move(board, this, candidate_destination_coordinate)));

                }
                else {
                    legal_moves.add(new Move.pawn_move(board, this, candidate_destination_coordinate));
                }
            }
            // maybe mixed second and 7 row... first move wont matter
            else if (offset == 16 &&
                    ((BoardUtils.is_seventh_rank(this.piece_position) && this.piece_colour.is_black()) ||
                     (BoardUtils.is_second_rank(this.piece_position) && this.piece_colour.is_white()))) {

                final int behind_destination = this.piece_position + (8 * (this.piece_colour.get_direction()));

                if (!board.get_tile(behind_destination).is_tile_used() &&
                    !board.get_tile(candidate_destination_coordinate).is_tile_used()) {
                    //TODO
                    legal_moves.add(new Move.pawn_jump(board, this, candidate_destination_coordinate));
                }
            }
            // bracket confuse
            else if (offset == 7 &&
                    !((BoardUtils.is_h_file(this.piece_position) && this.piece_colour.is_white()
                    ||(BoardUtils.is_a_file(this.piece_position) && this.piece_colour.is_black())))){
                if (board.get_tile(candidate_destination_coordinate).is_tile_used()){
                    final Piece piece_on_candidate = board.get_tile(candidate_destination_coordinate).get_piece();
                    if(this.piece_colour != piece_on_candidate.get_piece_colour()){
                        if (this.piece_colour.is_pawn_promotion_square(candidate_destination_coordinate)){
                            legal_moves.add(new Move.pawn_promotion(new Move.pawn_attack_move(board, this, candidate_destination_coordinate, piece_on_candidate)));
                        }
                        else {
                            legal_moves.add(new Move.pawn_attack_move(board, this, candidate_destination_coordinate, piece_on_candidate));
                        }
                    }
                }
                else if(board.get_en_passant_pawn() != null){
                    if (board.get_en_passant_pawn().get_piece_position() == (this.get_piece_position() +
                            (this.get_piece_colour().get_opposite_direction()))){
                        final Piece piece_on_candidate = board.get_en_passant_pawn();
                        if(this.get_piece_colour() != piece_on_candidate.get_piece_colour()){
                            legal_moves.add(new Move.En_Passant(board, this, candidate_destination_coordinate, piece_on_candidate));
                        }
                    }
                }
            }
            else if (offset == 9 &&
                    !((BoardUtils.is_h_file(this.piece_position) && this.piece_colour.is_black()
                     || (BoardUtils.is_a_file(this.piece_position) && this.piece_colour.is_white())))){
                if (board.get_tile(candidate_destination_coordinate).is_tile_used()){
                    final Piece piece_on_candidate = board.get_tile(candidate_destination_coordinate).get_piece();
                    if(this.piece_colour != piece_on_candidate.get_piece_colour()){
                        if (this.piece_colour.is_pawn_promotion_square(candidate_destination_coordinate)){
                            legal_moves.add(new Move.pawn_promotion(new Move.pawn_attack_move(board, this, candidate_destination_coordinate, piece_on_candidate)));
                        }
                        else {
                            legal_moves.add(new Move.pawn_attack_move(board, this, candidate_destination_coordinate, piece_on_candidate));
                        }
                    }
                }
                else if(board.get_en_passant_pawn() != null){
                    if (board.get_en_passant_pawn().get_piece_position() == (this.get_piece_position() +
                            (this.get_piece_colour().get_direction()))){
                        final Piece piece_on_candidate = board.get_en_passant_pawn();
                        if(this.get_piece_colour() != piece_on_candidate.get_piece_colour()){
                            legal_moves.add(new Move.En_Passant(board, this, candidate_destination_coordinate, piece_on_candidate));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legal_moves);
    }
    @Override
    public Pawn Move_piece(Move move) {
        return new Pawn(move.get_moved_piece().get_piece_colour(), move.Get_Destination());
    }
    @Override
    public String toString() {
        return Piece_Type.PAWN.toString();
    }
    @Override
    public int locationBonus() {
        return this.piece_colour.pawnBonus(this.piece_position);
    }
    public Piece get_promotion_piece(){
        return new Queen(this.piece_colour, this.piece_position, false);
    }

}
