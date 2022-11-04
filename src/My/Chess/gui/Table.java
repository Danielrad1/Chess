package My.Chess.gui;

import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;
import My.Chess.Engine.board.Tile;
import My.Chess.Engine.player.Move_Transition;
import My.Chess.Engine.player.ai.Mini_Max;
import My.Chess.Engine.player.ai.Move_Strategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.*;

public class Table extends Observable {


    private final JFrame game_frame;
    private final Game_History_Panel game_history_panel;
    private final Taken_Pieces_Panel taken_pieces_panel;
    private final DebugPanel debugPanel;
    private final Board_panel board_panel;
    private Board chess_board;
    private final Move_Log move_log;
    private final GameSetup gameSetup;

    private Tile source_tile;
    private Tile destination_tile;
    private Piece human_moved_piece;
    private Board_Direction board_direction;
    private Move computerMove;
    private boolean highlight_legal_moves;
    private boolean useBook;

    //play with these
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);
    private final static Dimension BOARD_PANEL_DIM = new Dimension(800, 800);
    private final static Dimension TILE_PANEL_DIM = new Dimension(10,10);
    public static final String default_piece_images_path = "art/simple/";
    public static final String fancy_piece_images_path = "art/fancy/";
    public static final String fancy2_piece_images_path = "art/fancy2/";
    public static final String warriors_piece_images_path = "art/holywarriors/";
    public static final String main_pieces_path = "art/MAIN/";

    private static final Table INSTANCE = new Table();


    private Table(){
        this.game_frame = new JFrame("Chess");
        this.game_frame.setLayout(new BorderLayout());
        final JMenuBar table_menu_bar = Create_menu_bar();
        this.game_frame.setJMenuBar(table_menu_bar);
        this.game_frame.setSize(OUTER_FRAME_DIMENSION);
        this.chess_board = Board.create_starting_position();
        this.game_history_panel = new Game_History_Panel();
        this.debugPanel = new DebugPanel();
        this.taken_pieces_panel = new Taken_Pieces_Panel();
        this.board_panel = new Board_panel();
        this.move_log = new Move_Log();
        this.addObserver(new Table_Game_Ai_Watcher());
        this.gameSetup = new GameSetup(this.game_frame, true);
        this.board_direction = Board_Direction.NORMAL;
        this.highlight_legal_moves = true;
        this.useBook = false;
        this.game_frame.add(this.taken_pieces_panel, BorderLayout.WEST);
        this.game_frame.add(this.board_panel, BorderLayout.CENTER);
        this.game_frame.add(this.game_history_panel, BorderLayout.EAST);
        this.game_frame.add(debugPanel, BorderLayout.SOUTH);
        this.game_frame.setVisible(true);
    }
    public static Table get(){
        return INSTANCE;
    }

    public void show(){
        invokeLater(new Runnable() {
            public void run() {
                Table.get().getMoveLog().clear();
                Table.get().getGameHistoryPanel().redo(chess_board, Table.get().getMoveLog());
                Table.get().getBoardPanel().draw_board(Table.get().getGameBoard());
                Table.get().getDebugPanel().redo();
            }
        });
        }

    private GameSetup getGameSetup(){return this.gameSetup;}

    private Board getGameBoard(){return this.chess_board;}

    DebugPanel getDebugPanel() {return this.debugPanel;}


    private JMenuBar Create_menu_bar() {
        final JMenuBar table_menu_bar = new JMenuBar();
        table_menu_bar.add(create_file_menu());
        table_menu_bar.add(create_preference_menu());
        table_menu_bar.add(create_options_menu());
        return table_menu_bar;
    }

    private JMenu create_file_menu() {

        final JMenu file_menu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open PGN File");
            }
        });
        file_menu.add(openPGN);
        final JMenuItem exit_menu_item = new JMenuItem("Exit");
        exit_menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file_menu.add(exit_menu_item);
        return file_menu;
    }

    private JMenu create_preference_menu(){
        final JMenu preference_menu = new JMenu("Preferences");
        final JMenuItem flip_board_menu_item = new JMenuItem("Flip Board");
        flip_board_menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board_direction = board_direction.opposite();
                board_panel.draw_board(chess_board);
            }
        });
        final JCheckBoxMenuItem legal_move_highlighter_checkbox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legal_move_highlighter_checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlight_legal_moves = legal_move_highlighter_checkbox.isSelected();
            }
        });

        final JCheckBoxMenuItem cbUseBookMoves = new JCheckBoxMenuItem(
                "Use Book Moves", false);

        cbUseBookMoves.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                useBook = cbUseBookMoves.isSelected();
            }
        });

        preference_menu.add(cbUseBookMoves);


        preference_menu.add(flip_board_menu_item);
        preference_menu.addSeparator();
        preference_menu.add(legal_move_highlighter_checkbox);

        return preference_menu;
    }

    private JMenu create_options_menu(){
        final JMenu options_menu = new JMenu("Options");
        final JMenuItem setup_game_menu_item = new JMenuItem("Setup Game");
        setup_game_menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        options_menu.add(setup_game_menu_item);
        return options_menu;
    }
    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);

    }
    private static class Table_Game_Ai_Watcher implements Observer{

        @Override
        public void update(final Observable o,
                           final Object arg) {

            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().current_player()) &&
                    !Table.get().getGameBoard().current_player().is_in_checkmate() &&
                    !Table.get().getGameBoard().current_player().is_in_stalemate()) {
                System.out.println(Table.get().getGameBoard().current_player() + " is set to AI, thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().current_player().is_in_checkmate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().current_player() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().current_player().is_in_stalemate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().current_player() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        }

    }

    private static class AIThinkTank extends SwingWorker<Move, String>{

        private AIThinkTank(){

        }

        @Override
        protected Move doInBackground() throws Exception {
            final Move bestMove;

                if (Table.get().getGameBoard().get_num_pieces() <= 10) {
                    final Move_Strategy miniMax = new Mini_Max(5);
                    bestMove = miniMax.execute(Table.get().getGameBoard());
                }
            else if (Table.get().getGameBoard().get_num_pieces() <= 5) {
                final Move_Strategy miniMax = new Mini_Max(6);
                bestMove = miniMax.execute(Table.get().getGameBoard());
            }

                else {
                    final Move_Strategy miniMax = new Mini_Max(4);
                    bestMove = miniMax.execute(Table.get().getGameBoard());
                }

             /*
            final int moveNumber = Table.get().getMoveLog().size();
            final int quiescenceFactor = 2000 + (100 * moveNumber);
            final AlphaBeta strategy = new AlphaBeta(quiescenceFactor);
            strategy.addObserver(Table.get().getDebugPanel());
            Table.get().getGameBoard().current_player().setMoveStrategy(strategy);
            bestMove = Table.get().getGameBoard().current_player().getMoveStrategy().execute(
                    Table.get().getGameBoard(), Table.get().getGameSetup().getSearchDepth());

              */
            return bestMove;
        }

        @Override
        protected void done() {
            try {
                final Move best_move = get();
                Table.get().updateComputerMove(best_move);
                Table.get().updateGameBoard(Table.get().getGameBoard().current_player().make_move(best_move).get_transition_board());
                Table.get().getMoveLog().add_move(best_move);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().draw_board(Table.get().getGameBoard());
                Table.get().getDebugPanel().redo();
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);


            } catch (InterruptedException e) {
                 e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean getUseBook() {return this.useBook;}

    private void updateGameBoard(final Board board) {this.chess_board = board;}
    public void updateComputerMove(final Move move){this.computerMove = move;}
    private Move_Log getMoveLog(){return this.move_log;}
    private Game_History_Panel getGameHistoryPanel(){return this.game_history_panel;}
    private Taken_Pieces_Panel getTakenPiecesPanel(){return this.taken_pieces_panel;}
    private Board_panel getBoardPanel() {return this.board_panel;}
    public void moveMadeUpdate(final PlayerType player_type){
        setChanged();
        notifyObservers(player_type);

    }


    public enum Board_Direction{
        NORMAL{
            @Override
            List<Tile_Panel> traverse(List<Tile_Panel> board_tiles) {
                return board_tiles;
            }

            @Override
            Board_Direction opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<Tile_Panel> traverse(List<Tile_Panel> board_tiles) {
                // potentially dangerous
                List<Tile_Panel> temp = new ArrayList<>(board_tiles);
                Collections.reverse(temp);
                return temp;
            }

            @Override
            Board_Direction opposite() {
                return NORMAL;
            }
        };
        abstract List<Tile_Panel> traverse(final List<Tile_Panel> board_tiles);
        abstract Board_Direction opposite();

    }

    private class Board_panel extends JPanel{

        final List<Tile_Panel> board_tiles;
        Board_panel(){
            super(new GridLayout(8,8));
            this.board_tiles = new ArrayList<>();

            for (int i = 0; i<64; i++){
                final Tile_Panel tile_panel = new Tile_Panel(this, i);
                this.board_tiles.add(tile_panel);
                add(tile_panel);
            }
            setPreferredSize(BOARD_PANEL_DIM);
            validate();
        }

        public void draw_board(final Board board) {
            removeAll();
            for (final Tile_Panel tile_panel: board_direction.traverse(board_tiles)){
                tile_panel.draw_tile(board);
                add(tile_panel);
            }
            validate();
            repaint();
        }
    }
    public static class Move_Log{
        private final List<Move> moves;
        Move_Log(){
            this.moves = new ArrayList<>();
        }
        public List<Move> get_moves(){return this.moves;}
        public void add_move(final Move move){this.moves.add(move);}
        public int size(){return this.moves.size();}
        public void clear(){this.moves.clear();}
        public Move remove_move (int index){return this.moves.remove(index);}
        public Boolean remove_move (final Move move){return this.moves.remove(move);}

    }

    enum PlayerType{
        HUMAN,
        COMPUTER;
    }
    private class Tile_Panel extends JPanel{
        private final int Tile_ID;

        Tile_Panel(final Board_panel board_panel, final int tile_ID){
            super(new GridBagLayout());
            this.Tile_ID = tile_ID;
            setPreferredSize(TILE_PANEL_DIM);
            assign_tile_color();
            assign_tile_piece_icon(chess_board);


            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        source_tile = null;
                        destination_tile = null;
                        human_moved_piece = null;
                        }
                    else if (isLeftMouseButton(e)) {
                        //System.out.println("recieved");
                        if (source_tile == null) {
                            source_tile = chess_board.get_tile(Tile_ID);
                            human_moved_piece = source_tile.get_piece();
                            if (human_moved_piece == null) source_tile = null;
                        }
                        else{
                            destination_tile = chess_board.get_tile(Tile_ID);

                            final Move move = Move.move_factory.create_move(chess_board,
                                    source_tile.get_tile_coordinate(), destination_tile.get_tile_coordinate());

                            final Move_Transition transition = chess_board.current_player().make_move(move);

                            if(transition.Get_move_status().is_done()){
                                chess_board = transition.get_transition_board();
                                move_log.add_move(move);
                            }
                            source_tile = null;
                            destination_tile = null;
                            human_moved_piece = null;
                        }
                        invokeLater(new Runnable() {
                            public void run() {
                                game_history_panel.redo(chess_board, move_log);
                                taken_pieces_panel.redo(move_log);
                                if (gameSetup.isAIPlayer(chess_board.current_player())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                board_panel.draw_board(chess_board);
                                debugPanel.redo();
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }
                @Override
                public void mouseReleased(final MouseEvent e) {
                }
                @Override
                public void mouseEntered(final MouseEvent e) {
                }
                @Override
                public void mouseExited(final MouseEvent e) {
                }
            });
            validate();
        }
        // USE FOR HIGHLIGHTING
        public void draw_tile(final Board board){
            assign_tile_color();
            assign_tile_piece_icon(board);
            highlight_legal_moves(chess_board);
            validate();
            repaint();
        }


        public void assign_tile_piece_icon(final Board board){
            this.removeAll();
            if(board.get_tile(this.Tile_ID).is_tile_used()){

                try {
                    // THEME
                    final BufferedImage image = ImageIO.read(new File(main_pieces_path +
                            board.get_tile(this.Tile_ID).get_piece().get_piece_colour().toString().substring(0,1) +
                            board.get_tile(this.Tile_ID).get_piece().toString() + ".png")); //.gif
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void highlight_legal_moves(final Board board){
            if (highlight_legal_moves){
                for (final Move move : piece_legal_moves(board)){
                    if (move.Get_Destination() == this.Tile_ID){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private Collection<Move> piece_legal_moves(final Board board){
            if(human_moved_piece!= null &&
            human_moved_piece.get_piece_colour() == board.current_player().get_colour()){
                return human_moved_piece.calculate_legal_moves(board);
            }
            return Collections.emptyList();
        }
        private void assign_tile_color() {

            boolean isLight = ((Tile_ID + Tile_ID / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);

            }
        }

    }


