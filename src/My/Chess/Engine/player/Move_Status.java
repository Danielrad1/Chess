package My.Chess.Engine.player;

public enum Move_Status {
    DONE{
        @Override
        public boolean is_done() {
            return true;
        }
    },
    ILLEGAL_MOVE{
        @Override
        public boolean is_done() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK{
        @Override
        public boolean is_done() {
            return false;
        }
    };

    public abstract boolean is_done();
}
