package My.Chess.Engine.player;

import My.Chess.Engine.Colour;
import My.Chess.Engine.Pieces.Queen;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.Pieces.King;
import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.player.ai.Move_Strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King player_king;
    protected final Queen player_queen;
    protected final Collection<Move> legal_moves;
    private Move_Strategy strategy;
    private final boolean is_in_check;

    Player(final Board board, final Collection<Move> legal_moves, final Collection<Move> opponent_moves){

        this.board = board;
        this.player_king = establish_king();
        this.player_queen = establish_queen();
        this.is_in_check = !Player.calculate_attacks_on_tile(this.player_king.get_piece_position(), opponent_moves).isEmpty();
        Collection<Move> castle_moves = calculate_king_castles(legal_moves, opponent_moves);

        legal_moves.addAll(castle_moves);
        this.legal_moves = Collections.unmodifiableCollection(legal_moves);
    }

    @Override
    public String toString() {
        if (this instanceof black_Player) return "Black";
        else return "White";
    }

    public King get_player_king(){return this.player_king;}
    public Collection<Move> get_legal_moves(){return this.legal_moves;}

    public static Collection<Move> calculate_attacks_on_tile(int piece_position, Collection<Move> moves) {
        final List<Move> attack_moves = new ArrayList<>();
        for (final Move move : moves){
            if (piece_position == move.Get_Destination()) attack_moves.add(move);
        }
        return Collections.unmodifiableList(attack_moves);
    }

    private King establish_king() {
        for (final Piece piece : get_active_pieces()){
            if (piece.get_piece_type().is_king()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board: No King");
    }
    private Queen establish_queen() {
        for (final Piece piece : get_active_pieces()){
            if (piece.get_piece_type().isqueen()){
                return (Queen) piece;
            }
        }
        return null;
    }

    public boolean is_legal_move(final Move move){
        return this.legal_moves.contains(move);
    }

    public boolean is_King_side_castle_capable(){
        return this.player_king.is_king_side_castle_capable();
    }
   public boolean is_Queen_side_castle_capable(){
        return this.player_king.is_queen_side_castle_capable();
   }
    public boolean is_in_check() {
        if (get_opponent() == null) return false;
        return this.player_king.is_in_check(this.get_opponent().get_legal_moves());
    }

    public boolean queen_is_in_check() {
        if (get_opponent() == null || player_queen == null) return false;
        return this.player_queen.is_in_queen_check(this.get_opponent().get_legal_moves());
    }

    public boolean is_in_checkmate() {
        return this.player_king.is_in_checkmate(this.board);
    }

    public boolean is_in_stalemate(){
        return this.player_king.is_in_stalemate(this.board);
    }

    // also has no moves

    public boolean is_castled(){
        return false;
    }
    public Move_Transition make_move(final Move move){
        if(!is_legal_move(move)){
            return new Move_Transition(this.board, move, Move_Status.ILLEGAL_MOVE);
        }
        final Board transition_board = move.execute();
        final Collection<Move> king_attacks = Player.calculate_attacks_on_tile(transition_board.current_player().get_opponent().get_player_king().get_piece_position(),
                transition_board.current_player().get_legal_moves());
        if (!king_attacks.isEmpty()){
            return new Move_Transition(this.board, move, Move_Status.LEAVES_PLAYER_IN_CHECK);
        }
        return new Move_Transition(transition_board, move, Move_Status.DONE);
    }
    public abstract Collection<Piece> get_active_pieces();
    public abstract Colour get_colour();
    public abstract Player get_opponent();

    protected abstract Collection<Move> calculate_king_castles(Collection<Move> player_legals, Collection<Move> opponent_legals);



}
