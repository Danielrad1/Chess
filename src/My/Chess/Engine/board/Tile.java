package My.Chess.Engine.board;

import My.Chess.Engine.Pieces.Piece;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final  int tile_coordinate;

    public static final Map<Integer, empty_tile> EMPTY_TILE_CACHE = create_empty_tile_map();

    public static Map<Integer, empty_tile> create_empty_tile_map(){
        final Map<Integer, empty_tile> empty_tile_map= new HashMap<>();

        for (int i = 0; i < BoardUtils.num_tiles; ++i){ //64
            empty_tile_map.put(i, new empty_tile(i));
        }

        return Collections.unmodifiableMap(empty_tile_map);// maybe not immutable?
    }

    public static Tile create_tile(final int coordinate, final Piece piece){
        if (piece == null) return EMPTY_TILE_CACHE.get(coordinate);
        else return new used_tile(coordinate, piece);
    }

    private Tile(final int Coordinate) {
        tile_coordinate = Coordinate;
    }

    public abstract boolean is_tile_used();

    public abstract Piece get_piece();

    public int get_tile_coordinate(){return this.tile_coordinate;}

    public static final class empty_tile extends Tile {

        private empty_tile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean is_tile_used() {
            return false;
        }

        @Override
        public Piece get_piece() {
            return null;
        }
    }

    public static final class used_tile extends Tile {

       private final  Piece piece_on_tile;

        private used_tile(final int coordinate, final Piece piece_on_tile) { // final?
            super(coordinate);
            this.piece_on_tile = piece_on_tile;
        }


        @Override
        public String toString() {
            if (get_piece().get_piece_colour().is_black()){
                return get_piece().toString().toLowerCase();
            }
            return get_piece().toString();
        }

        @Override
        public boolean is_tile_used() {
            return true;
        }

        @Override
        public Piece get_piece() {
            return this.piece_on_tile;
        }


    }
}
