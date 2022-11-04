package My.Chess.Engine.Pieces;

import My.Chess.Engine.Colour;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final Piece_Type piece_Type;

    protected final int piece_position;
    protected final Colour piece_colour;

    protected final boolean is_first_move;
    private final int cachedHashCode;

    Piece (final Piece_Type piece_Type, final int position, final Colour colour, final boolean is_first_move){
        this.piece_position = position;
        this.piece_colour = colour;
        this.piece_Type = piece_Type;


        this.is_first_move = is_first_move;
        this.cachedHashCode = compute_hash_code();
    }

    private int compute_hash_code() {
        int result = piece_Type.hashCode();
        result = 31 * result + piece_colour.hashCode();
        result = 31 * result + piece_position;
        result = 31 * result + (is_first_move ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this==other) return true;
        if(!(other instanceof Piece)) return false;

        final Piece other_piece = (Piece) other;
        return ((piece_colour == other_piece.get_piece_colour()) && (piece_Type == other_piece.get_piece_type())
                && (piece_position == other_piece.get_piece_position()) && (is_first_move == other_piece.is_first_move));

    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public Colour get_piece_colour(){
        return this.piece_colour;
    }

    public int get_piece_position(){
        return this.piece_position;
    }

    public boolean is_first_move() {return this.is_first_move;}

    public Piece_Type get_piece_type(){return this.piece_Type;}
    public int get_piece_value(){return this.piece_Type.get_Piece_value();}

    public abstract Collection<Move> calculate_legal_moves(final Board board);
    public abstract Piece Move_piece(final Move move);

    public abstract int locationBonus();

    public enum Piece_Type {

        PAWN("P", 100){
            @Override
            public boolean is_king() {return false;}

            @Override
            public boolean isPawn() {
                return true;
            }

            @Override
            public boolean is_rook() {
                return false;
            }

            @Override
            public boolean isqueen() {
                return false;
            }
        },
        KNIGHT("N", 300){
            @Override
            public boolean is_king() {return false;}

            @Override
            public boolean is_rook() {
                return false;
            }
            @Override
            public boolean isPawn() {return false;}
            public boolean isqueen() {return false;}
        },
        ROOK("R", 500){
            @Override
            public boolean is_king() {return false;}
            @Override
            public boolean is_rook() {
                return true;
            }
            public boolean isPawn() {return false;}
            public boolean isqueen() {return false;}
        },
        BISHOP("B",330){
            @Override
            public boolean is_king() {return false;}
            @Override
            public boolean is_rook() {
                return false;
            }
            public boolean isPawn() {return false;}
            public boolean isqueen() {return false;}
        },
        KING("K", 10000){
            @Override
            public boolean is_king() {return true;}
            @Override
            public boolean is_rook() {
                return false;
            }
            public boolean isPawn() {return false;}
            public boolean isqueen() {return false;}
        },
        QUEEN("Q", 900){
            @Override
            public boolean is_king() {return false;}
            public boolean isPawn() {return false;}
            @Override
            public boolean is_rook() {
                return false;
            }
            public boolean isqueen() {return true;}
        };
        private final String piece_name;
        private int piece_value;
        // careful
        Piece_Type(final String piece_name, final int piece_value){
            this.piece_name = piece_name;
            this.piece_value = piece_value;
        }
        @Override
        public String toString() {
            return this.piece_name;
        }
        public int get_Piece_value(){
            return this.piece_value;
        }
        public abstract boolean is_king();
        public abstract boolean isPawn();
        public abstract boolean is_rook();

        public abstract boolean isqueen();
    }
}
