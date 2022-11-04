package My.Chess.Engine.board;

import My.Chess.Engine.Colour;
import My.Chess.Engine.Pieces.*;
import My.Chess.Engine.Pieces.*;
import My.Chess.Engine.player.Player;
import My.Chess.Engine.player.black_Player;
import My.Chess.Engine.player.white_Player;

import java.util.*;

public class Board {

    private final List<Tile> game_board;
    private final Collection<Piece> white_pieces;
    private final Collection<Piece> black_pieces;
    private final white_Player white_player;
    private final black_Player black_player;
    private final Player current_player;
    private final Pawn en_passant_pawn;

    private Board (final Builder builder){
        this.game_board = create_game_board(builder);
        this.white_pieces = calculate_active_pieces(this.game_board, Colour.WHITE);
        this.black_pieces = calculate_active_pieces(this.game_board, Colour.BLACK);
        this.en_passant_pawn = builder.en_passant_pawn;

        final Collection<Move> white_legal_moves = calculate_all_legal_moves(this.white_pieces);
        final Collection<Move> black_legal_moves = calculate_all_legal_moves(this.black_pieces);

        this.white_player = new white_Player(this, white_legal_moves,black_legal_moves);
        this.black_player = new black_Player(this, white_legal_moves, black_legal_moves);
        this.current_player = builder.next_move_maker.choose_player(this.white_player, this.black_player);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i< 64; i++){
            final String tile_text = this.game_board.get(i).toString();
            builder.append(String.format("%3s", tile_text));
            if ((i +1) % 8 == 0) builder.append("\n");

        }
        return builder.toString();
    }

    public Player white_player(){
        return this.white_player;
    }
    public Player black_player(){
        return this.black_player;
    }

    public Player current_player(){
        return this.current_player;
    }

    public Collection<Piece> get_white_pieces(){
        return this.white_pieces;
    }

    public Collection<Piece> get_black_pieces(){
        return this.black_pieces;
    }

    public Pawn get_en_passant_pawn(){return this.en_passant_pawn;}


    private Collection<Move> calculate_all_legal_moves(final Collection<Piece> pieces) {
        final List<Move> all_legal_moves = new ArrayList<>();

        for (final Piece piece : pieces){
            all_legal_moves.addAll(piece.calculate_legal_moves(this));
        }
        // CHANGES IT TO MODIFIABLE CAUSE SHOT BUGS
        return (all_legal_moves);

    }

    private static Collection<Piece> calculate_active_pieces(final List<Tile> game_board, final Colour colour) {

        final List<Piece> active_pieces = new ArrayList<>();

        for (final Tile tile: game_board){
            if (tile.is_tile_used() && (tile.get_piece().get_piece_colour() == colour)){
                active_pieces.add(tile.get_piece());
            }
        }
        return Collections.unmodifiableList(active_pieces);

    }

    public Tile get_tile(final int coordinate){
        return game_board.get(coordinate);
    }

    private static List<Tile> create_game_board(final Builder builder) {

        final Tile[] tiles = new Tile[64];
        for (int i = 0; i < 64; i++) {
            tiles[i] = Tile.create_tile(i, builder.current_board.get(i));
        }
        return Collections.unmodifiableList(Arrays.asList(tiles));
    }

    public static Board create_starting_position(){
        final Builder builder = new Builder();

        //Black Layout

        builder.set_piece(new Rook(Colour.BLACK, 0));
        builder.set_piece(new Knight(Colour.BLACK, 1));
        builder.set_piece(new Bishop(Colour.BLACK, 2));
        builder.set_piece(new Queen(Colour.BLACK, 3));
        builder.set_piece(new King(Colour.BLACK, 4, true, true));
        builder.set_piece(new Bishop(Colour.BLACK, 5));
        builder.set_piece(new Knight(Colour.BLACK, 6));
        builder.set_piece(new Rook(Colour.BLACK, 7));
        builder.set_piece(new Pawn(Colour.BLACK, 8));
        builder.set_piece(new Pawn(Colour.BLACK, 9));
        builder.set_piece(new Pawn(Colour.BLACK, 10));
        builder.set_piece(new Pawn(Colour.BLACK, 11));
        builder.set_piece(new Pawn(Colour.BLACK, 12));
        builder.set_piece(new Pawn(Colour.BLACK, 13));
        builder.set_piece(new Pawn(Colour.BLACK, 14));
        builder.set_piece(new Pawn(Colour.BLACK, 15));

        // White Layout
        builder.set_piece(new Pawn(Colour.WHITE, 48));
        builder.set_piece(new Pawn(Colour.WHITE, 49));
        builder.set_piece(new Pawn(Colour.WHITE, 50));
        builder.set_piece(new Pawn(Colour.WHITE, 51));
        builder.set_piece(new Pawn(Colour.WHITE, 52));
        builder.set_piece(new Pawn(Colour.WHITE, 53));
        builder.set_piece(new Pawn(Colour.WHITE, 54));
        builder.set_piece(new Pawn(Colour.WHITE, 55));
        builder.set_piece(new Rook(Colour.WHITE, 56));
        builder.set_piece(new Knight(Colour.WHITE, 57));
        builder.set_piece(new Bishop(Colour.WHITE, 58));
        builder.set_piece(new Queen(Colour.WHITE, 59));
        builder.set_piece(new King(Colour.WHITE, 60, true, true));
        builder.set_piece(new Bishop(Colour.WHITE, 61));
        builder.set_piece(new Knight(Colour.WHITE, 62));
        builder.set_piece(new Rook(Colour.WHITE, 63));


        builder.set_move_maker(Colour.WHITE);

        return builder.build();
    }

    public Iterable<Move> get_all_legal_moves() {

        List<Move> allLegalMoves = new ArrayList<>();
        allLegalMoves.addAll(this.white_player.get_legal_moves());
        allLegalMoves.addAll(this.black_player.get_legal_moves());
        return Collections.unmodifiableList(allLegalMoves);
    }
    public long get_num_pieces(){

        return get_black_pieces().size() + get_white_pieces().size();
    }

    public static class Builder{
        Map<Integer, Piece> current_board;
        Colour next_move_maker;
        Pawn en_passant_pawn;

        public Builder(){
            this.current_board = new HashMap<>();
        }

        public Builder set_piece(final Piece piece){
            this.current_board.put(piece.get_piece_position(), piece);
            return this;
        }

        public Builder set_move_maker(final Colour colour){
            this.next_move_maker = colour;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void set_en_passant(Pawn pawn) {
            this.en_passant_pawn = pawn;
        }
    }
}
