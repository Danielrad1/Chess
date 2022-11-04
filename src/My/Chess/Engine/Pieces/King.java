package My.Chess.Engine.Pieces;

import My.Chess.Engine.Colour;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.BoardUtils;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.board.Tile;
import My.Chess.Engine.player.Move_Transition;
import My.Chess.Engine.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class King extends Piece {

    private final static int[] candidate_move_vectors = {8, 7, 9, -1, 1, -8, -7, -9};
    private final boolean is_castled;
    private final boolean king_side_castle_capable;
    private final boolean queen_side_castle_capable;

    public King(final Colour colour, final int position, final boolean king_side_castle, final boolean queen_side_castle) {
        super(Piece_Type.KING,position, colour,true);
        this.is_castled = false;
        this.king_side_castle_capable = king_side_castle;
        this.queen_side_castle_capable = queen_side_castle;

    }
    public King(final Colour colour, final int position, final boolean is_first_move, final boolean is_castled,
                final boolean king_side_castle, final boolean queen_side_castle) {
        super(Piece_Type.KING, position, colour, is_first_move);
        this.is_castled = is_castled;
        this.king_side_castle_capable = king_side_castle;
        this.queen_side_castle_capable = queen_side_castle;
    }

    public boolean Is_castled(){
        return this.is_castled;
    }
    public boolean is_queen_side_castle_capable(){
        return this.queen_side_castle_capable;
    }
    public boolean is_king_side_castle_capable(){
        return this.king_side_castle_capable;
    }

    @Override
    public Collection<Move> calculate_legal_moves(Board board) {

        final List<Move> legal_moves = new ArrayList<>();

        for (final int offset : candidate_move_vectors) {
           final int candidate_destination_coordinate = this.piece_position + offset;

           if (BoardUtils.is_valid_tile_coordinate(candidate_destination_coordinate)){

               final Tile candidate_destination_tile = board.get_tile(candidate_destination_coordinate);

               if(is_first_column_exception(this.piece_position,offset) ||
                  is_eighth_column_exception(this.piece_position, offset)){
                   continue;
               }

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
    public King Move_piece(Move move) {
        return new King(move.get_moved_piece().get_piece_colour(),
                move.Get_Destination(), false, move.is_castling_move(), false, false);
    }

    @Override
    public int locationBonus() {
        return this.piece_colour.kingBonus(this.piece_position);
    }

    @Override
    public String toString() {
        return Piece_Type.KING.toString();
    }
    private static boolean is_first_column_exception(final int position, final int offset){
        return BoardUtils.is_a_file(position) && (offset == -9 || offset == -1 || offset == 7);
    }
    private static boolean is_eighth_column_exception(final int position, final int offset){
        return BoardUtils.is_h_file(position) && (offset == 9 || offset == 1 || offset == -7);
    }

    private static boolean king_has_escape_moves(final Board board) {
        for(final Move move : board.current_player().get_player_king().calculate_legal_moves(board)) {
            final Move_Transition transition = board.current_player().make_move(move);
            if (transition.Get_move_status().is_done()) {
                return true;
            }
        }
        return false;
    }
    private static boolean has_escape_moves(final Board board) {
        for(final Move move : board.current_player().get_legal_moves()) {
            final Move_Transition transition = board.current_player().make_move(move);
            if (transition.Get_move_status().is_done()) {
                return true;
            }
        }
        return false;
    }

    public boolean is_in_check(final Collection<Move> enemyMoves) {
        return !Player.calculate_attacks_on_tile(this.piece_position, enemyMoves).isEmpty();
    }

    public boolean is_in_checkmate(final Board board) {
        return board.current_player().is_in_check() && !has_escape_moves(board);
    }


    public boolean is_in_stalemate(final Board board) {


        return (board.current_player().get_active_pieces().size() <5 && !board.current_player().is_in_check() && !king_has_escape_moves(board));
    }
}
