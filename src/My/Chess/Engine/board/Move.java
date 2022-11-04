package My.Chess.Engine.board;

import My.Chess.Engine.Pieces.Pawn;
import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.Pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece moved_piece;
    protected final int destination_coordinate;
    protected final boolean is_first_move;

    public static final Move NULL_MOVE = new null_move();
    private Move(final Board board, final Piece moved_piece, final int destination_coordinate) {
        this.board = board;
        this.moved_piece = moved_piece;
        this.destination_coordinate = destination_coordinate;
        this.is_first_move = moved_piece.is_first_move();
    }

    private Move(final Board board, final int destination_coordinate){

        this.board = board;
        this.destination_coordinate = destination_coordinate;
        this.moved_piece = null;
        this.is_first_move = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destination_coordinate;
        result = prime * result + this.moved_piece.hashCode();
        result = prime * result + this.moved_piece.get_piece_position();

        return result;
    }
    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof Move)) return false;

        final Move other_move = (Move) other;
        return get_current_coordinate() == other_move.get_current_coordinate() &&
                Get_Destination() == other_move.Get_Destination()
                && get_moved_piece().equals(other_move.get_moved_piece());
    }
    public Board get_board(){
        return this.board;
    }
    //TODO CHANGED FOR BUG
    public int get_current_coordinate(){
        if (this.moved_piece == null) return -1;
        return this.moved_piece.get_piece_position();}
    public int Get_Destination() {return this.destination_coordinate;}
    public Piece get_moved_piece() {return this.moved_piece;}
    public boolean is_attack() {return false;}
    public boolean is_castling_move() {return false;}
    public Piece get_attacked_piece() {return null;}

    public Board execute() {
        final Board.Builder builder = new Board.Builder();

        for (final Piece piece : this.board.current_player().get_active_pieces()) {
            if (!this.moved_piece.equals(piece)) {
                builder.set_piece(piece);
            }
        }
        for (final Piece piece : this.board.current_player().get_opponent().get_active_pieces()) {
            builder.set_piece(piece);
        }
        // move the moved piece
        builder.set_piece(this.moved_piece.Move_piece(this));
        builder.set_move_maker(this.board.current_player().get_opponent().get_colour());

        return builder.build();
    }

    public static final class major_attack_move extends attacking_move{
        public major_attack_move(final Board board, final Piece piece_moved,
                                 final int destination_coordinate, final Piece piece_attacked){
            super(board, piece_moved, destination_coordinate, piece_attacked);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof major_attack_move && super.equals(other);
        }

        @Override
        public String toString() {
            return moved_piece.get_piece_type() + "x" + BoardUtils.get_position_at_coordinate(this.destination_coordinate);
        }
    }
    public static final class major_move extends Move {
        public major_move(final Board board, final Piece moved_piece, final int destination_coordinate) {
            super(board, moved_piece, destination_coordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof major_move && super.equals(other);
        }

        @Override
        public String toString() {
            return moved_piece.get_piece_type().toString() + BoardUtils.get_position_at_coordinate(this.destination_coordinate);
        }
    }
    public static class attacking_move extends Move {
        final Piece attacked_piece;
        public attacking_move(final Board board, final Piece moved_piece,
                              final int destination_coordinate, final Piece attacked_piece) {

            super(board, moved_piece, destination_coordinate);
            this.attacked_piece = attacked_piece;
        }

        @Override
        public int hashCode() {return this.attacked_piece.hashCode() + super.hashCode();}

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof attacking_move)) return false;

            final attacking_move other_attack_move = (attacking_move) other;
            return super.equals(other_attack_move) && get_attacked_piece().equals(other_attack_move.get_attacked_piece());
        }

        //@Override
        // TODO
        //public Board execute() {return null;}

        @Override
        public boolean is_attack() {return true;}

        @Override
        public Piece get_attacked_piece() {return this.attacked_piece;}
    }

    public static final class pawn_move extends Move {
        public pawn_move(Board board, Piece moved_piece, int destination_coordinate) {
            super(board, moved_piece, destination_coordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof pawn_move && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.get_position_at_coordinate(this.destination_coordinate);
        }
    }

    public static class pawn_attack_move extends attacking_move {
        public pawn_attack_move(Board board, Piece moved_piece, int destination_coordinate, Piece attacked_piece) {
            super(board, moved_piece, destination_coordinate, attacked_piece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof pawn_attack_move && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.get_position_at_coordinate(this.moved_piece.get_piece_position()).charAt(0)
                    + "x" + BoardUtils.get_position_at_coordinate(this.destination_coordinate);
        }
    }

    public static class En_Passant extends pawn_attack_move {
        public En_Passant(Board board, Piece moved_piece, int destination_coordinate, Piece attacked_piece) {
            super(board, moved_piece, destination_coordinate, attacked_piece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof En_Passant && super.equals(other);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.current_player().get_active_pieces()){
                if (!this.moved_piece.equals(piece)){
                    builder.set_piece(piece);
                }
            }
            for (final Piece piece : this.board.current_player().get_opponent().get_active_pieces()){
                if(!piece.equals(this.get_attacked_piece())){
                    builder.set_piece(piece);
                }
            }
            builder.set_piece(this.moved_piece.Move_piece(this));
            builder.set_move_maker(this.board.current_player().get_opponent().get_colour());
            return builder.build();

        }
    }

    public static class pawn_promotion extends Move{

        final Move decorate_move;
        final Pawn promoted_pawn;

        public pawn_promotion(final Move decorate_move){
            super(decorate_move.get_board(), decorate_move.get_moved_piece(),decorate_move.Get_Destination());
            this.decorate_move = decorate_move;
            this.promoted_pawn = (Pawn) decorate_move.get_moved_piece();
        }

        @Override
        public int hashCode() {
            return decorate_move.hashCode() + (31 * promoted_pawn.hashCode());
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof pawn_promotion && super .equals(other);
        }

        @Override
        public Board execute() {
            final Board pawn_moved_board = this.decorate_move.execute();
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : pawn_moved_board.current_player().get_active_pieces()){
                if (!this.promoted_pawn.equals(piece)){
                    builder.set_piece(piece);
                }
            }
            for (final Piece piece : pawn_moved_board.current_player().get_opponent().get_active_pieces()){
                    builder.set_piece(piece);
            }
            builder.set_piece(this.promoted_pawn.get_promotion_piece().Move_piece(this));
            builder.set_move_maker(pawn_moved_board.current_player().get_colour());
            return builder.build();
        }

        @Override
        public boolean is_attack() {
            return this.decorate_move.is_attack();
        }

        @Override
        public Piece get_attacked_piece() {
            return this.decorate_move.get_attacked_piece();
        }

        @Override
        public String toString() {
            return BoardUtils.get_position_at_coordinate(this.destination_coordinate) + "=Q";
        }
    }
    public static final class pawn_jump extends Move {
        public pawn_jump(Board board, Piece moved_piece, int destination_coordinate) {
            super(board, moved_piece, destination_coordinate);
        }
        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.current_player().get_active_pieces()) {
                if (!this.moved_piece.equals(piece)) {
                    builder.set_piece(piece);
                }
            }
            for (final Piece piece : this.board.current_player().get_opponent().get_active_pieces()) {
                builder.set_piece(piece);
            }
            final Pawn moved_pawn = (Pawn) this.moved_piece.Move_piece(this);
            builder.set_piece(moved_pawn);
            builder.set_en_passant(moved_pawn);
            builder.set_move_maker(this.board.current_player().get_opponent().get_colour());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.get_position_at_coordinate(this.destination_coordinate);
        }
    }

    static abstract class castle_move extends Move {

        protected final Rook castle_rook;
        protected final int castle_rook_start;
        protected final int castle_rook_destination;

        public castle_move(final Board board, final Piece moved_piece, final int destination_coordinate,
                           final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination) {
            super(board, moved_piece, destination_coordinate);
            this.castle_rook = castle_rook;
            this.castle_rook_start = castle_rook_start;
            this.castle_rook_destination = castle_rook_destination;
        }

        public Rook get_castle_rook() {
            return this.castle_rook;
        }

        @Override
        public boolean is_castling_move() {return true;}

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.current_player().get_active_pieces()) {
                if (!this.moved_piece.equals(piece) && !this.castle_rook.equals(piece)) {
                    builder.set_piece(piece);
                }
            }
            for (final Piece piece : this.board.current_player().get_opponent().get_active_pieces()) {
                    builder.set_piece(piece);

            }
            builder.set_piece(this.moved_piece.Move_piece(this));
            // TODO
            builder.set_piece(new Rook(this.castle_rook.get_piece_colour(), this.castle_rook_destination));
            builder.set_move_maker(this.board.current_player().get_opponent().get_colour());

            return builder.build();
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.castle_rook.hashCode();
            result = 31 * result + this.castle_rook_destination;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof castle_move)) return false;
            final castle_move other_castle_move = (castle_move) other;
            return super.equals(other_castle_move) && this.castle_rook.equals(other_castle_move.get_castle_rook());
        }
    }

        public static final class king_side_castle extends castle_move {
            public king_side_castle(Board board, Piece moved_piece, int destination_coordinate,
                                    final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination) {
                super(board, moved_piece, destination_coordinate, castle_rook, castle_rook_start, castle_rook_destination);
            }

            @Override
            public boolean equals(Object other) {
                return this == other || other instanceof king_side_castle && super.equals(other);
            }

            @Override
            public String toString() {return "O-O";}
        }

        public static final class queen_side_castle extends castle_move {
            public queen_side_castle(Board board, Piece moved_piece, int destination_coordinate,
                                     final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination) {
                super(board, moved_piece, destination_coordinate, castle_rook, castle_rook_start, castle_rook_destination);
            }

            @Override
            public String toString() {return "O-O-O";}
            @Override
            public boolean equals(Object other) {
                return this == other || other instanceof queen_side_castle && super.equals(other);
            }
        }

        public static final class null_move extends Move {
            public null_move() {super(null,-1);}
            @Override
            public Board execute() {throw new RuntimeException("invalid move");}

            @Override
            public int Get_Destination() {
                return -1;
            }
        }

        public static class move_factory {
            private move_factory() {throw new RuntimeException("can't instantiate");}

            public static Move create_move(final Board board, final int current_coordinate, final int destination_coordinate) {
                for (final Move move : board.get_all_legal_moves()) {
                    if (move.get_current_coordinate() == current_coordinate &&
                            move.Get_Destination() == destination_coordinate) {
                        return move;
                    }
                }
                return NULL_MOVE;
            }
    }
}
