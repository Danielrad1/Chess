package My.Chess.Engine;

import My.Chess.Engine.board.BoardUtils;
import My.Chess.Engine.player.Player;
import My.Chess.Engine.player.black_Player;
import My.Chess.Engine.player.white_Player;

public enum Colour {
    WHITE{
        @Override
        public int get_direction() {
            return -1;
        }

        @Override
        public int get_opposite_direction() {
            return 1;
        }

        @Override
        public boolean is_white() {
            return true;
        }
        @Override
        public boolean is_black() {
            return false;
        }

        @Override
        public boolean is_pawn_promotion_square(int position) {
            return BoardUtils.is_eighth_rank(position);
        }

        @Override
        public Player choose_player(final white_Player white, final black_Player black) {
            return white;
        }

        @Override
        public int pawnBonus(final int position) {
            return WHITE_PAWN_PREFERRED_COORDINATES[position];
        }

        @Override
        public int knightBonus(final int position) {
            return WHITE_KNIGHT_PREFERRED_COORDINATES[position];
        }

        @Override
        public int bishopBonus(final int position) {
            return WHITE_BISHOP_PREFERRED_COORDINATES[position];
        }

        @Override
        public int rookBonus(final int position) {
            return WHITE_ROOK_PREFERRED_COORDINATES[position];
        }

        @Override
        public int queenBonus(final int position) {
            return WHITE_QUEEN_PREFERRED_COORDINATES[position];
        }

        @Override
        public int kingBonus(final int position) {
            return WHITE_KING_PREFERRED_COORDINATES[position];
        }

    },
    BLACK {
        @Override
        public int get_direction () {
            return 1;
        }

        @Override
        public int get_opposite_direction() {
            return -1;
        }

        @Override
        public boolean is_white() {
            return false;
        }

        @Override
        public boolean is_black() {
            return true;
        }

        @Override
        public boolean is_pawn_promotion_square(int position) {
            return BoardUtils.is_first_rank(position);
        }

        @Override
        public Player choose_player(final white_Player white, final black_Player black) {
            return black;
        }

        @Override
        public int pawnBonus(final int position) {
            return BLACK_PAWN_PREFERRED_COORDINATES[position];
        }

        @Override
        public int knightBonus(final int position) {
            return BLACK_KNIGHT_PREFERRED_COORDINATES[position];
        }

        @Override
        public int bishopBonus(final int position) {
            return BLACK_BISHOP_PREFERRED_COORDINATES[position];
        }

        @Override
        public int rookBonus(final int position) {
            return BLACK_ROOK_PREFERRED_COORDINATES[position];
        }

        @Override
        public int queenBonus(final int position) {
            return BLACK_QUEEN_PREFERRED_COORDINATES[position];
        }

        @Override
        public int kingBonus(final int position) {
            return BLACK_KING_PREFERRED_COORDINATES[position];
        }
    };

    public abstract int get_direction();
    public abstract int get_opposite_direction();

    public abstract boolean is_white();
    public abstract boolean is_black();
    public abstract boolean is_pawn_promotion_square(int position);

    public abstract Player choose_player(final white_Player white, final black_Player black);

    public abstract int pawnBonus(int position);

    public abstract int knightBonus(int position);

    public abstract int bishopBonus(int position);

    public abstract int rookBonus(int position);

    public abstract int queenBonus(int position);

    public abstract int kingBonus(int position);

    private final static int[] WHITE_PAWN_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  -15,  0,  0,
            5, -5,-10,  -5,  -15,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] BLACK_PAWN_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10,-20,-20, 10, 10,  5,
            5, -5,-10,  -5,  -5,-15, -5,  5,
            0,  0,  0, 20, 20,  -15,  0,  0,
            5,  5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] WHITE_KNIGHT_PREFERRED_COORDINATES = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 35,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 20,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-100,-30,-30,-30,-30,-100,-50
    };

    private final static int[] BLACK_KNIGHT_PREFERRED_COORDINATES = {
            -50,-100,-30,-30,-30,-30,-100,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 20, 15, 15, 10,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 35, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private final static int[] WHITE_BISHOP_PREFERRED_COORDINATES = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  40,  0,  0,  0,  0,  40,-10,
            -20,-10,-100,-10,-10,-100,-10,-20
    };

    private final static int[] BLACK_BISHOP_PREFERRED_COORDINATES = {
            -20,-10,-100,-10,-10,-100,-10,-20,
            -10,  40,  0,  0,  0,  0,  40,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };

    private final static int[] WHITE_ROOK_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 20, 20, 20, 20, 20, 20,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -20,  0,  0,  5,  5,  0,  0,  -20
    };

    private final static int[] BLACK_ROOK_PREFERRED_COORDINATES = {
            -20,  0,  0,  5,  5,  0,  0,  -20,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 20, 20, 20, 20, 20, 20,  5,
            0,  0,  0,  0,  0,  0,  0,  0,
    };

    private final static int[] WHITE_QUEEN_PREFERRED_COORDINATES = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, 40, -5,-10,-10,-20
    };

    private final static int[] BLACK_QUEEN_PREFERRED_COORDINATES = {
            -20,-10,-10, 40, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
            0,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private final static int[] WHITE_KING_PREFERRED_COORDINATES = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 60, 50,  0,  0, 10, 50, 20
    };

    private final static int[] BLACK_KING_PREFERRED_COORDINATES = {
            20, 60, 50,  0,  0, 10, 50, 20,
            20, 20,  0,  0,  0,  0, 20, 20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30
    };

    private static final int UP_DIRECTION = -1;

    private static final int DOWN_DIRECTION = 1;

}

