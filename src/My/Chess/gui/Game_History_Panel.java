package My.Chess.gui;

import My.Chess.Engine.board.Board;
import My.Chess.Engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Game_History_Panel extends JPanel {
    private final Data_Model model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    Game_History_Panel(){
        this.setLayout(new BorderLayout());
        this.model = new Data_Model();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final Table.Move_Log move_history){
        int current_row = 0;
        this.model.clear();
        for(final Move move: move_history.get_moves()){
            final String move_text = move.toString();
            if(move.get_moved_piece().get_piece_colour().is_white()){
                this.model.setValueAt(move_text, current_row, 0);
            }
            else if (move.get_moved_piece().get_piece_colour().is_black()){
                this.model.setValueAt(move_text, current_row, 1);
                current_row ++;
            }
        }
        if(move_history.get_moves().size() > 0){
            final Move last_Move = move_history.get_moves().get(move_history.size() - 1);
            final String move_text = last_Move.toString();

            if(last_Move.get_moved_piece().get_piece_colour().is_white()){
                this.model.setValueAt(move_text + calculate_check_and_checkmate_hash(board), current_row, 0);
            }
            else if(last_Move.get_moved_piece().get_piece_colour().is_black()){
                this.model.setValueAt(move_text + calculate_check_and_checkmate_hash(board), current_row - 1, 1);
            }
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private String calculate_check_and_checkmate_hash(final Board board) {
        if(board.current_player().is_in_checkmate()) return "#";
        else if (board.current_player().is_in_check()) return "+";
        else return "";
    }

    private static class Data_Model extends DefaultTableModel{
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};
        Data_Model(){
            this.values = new ArrayList<>();
        }
        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null) return 0;
            else return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row current_row = this.values.get(row);
            if(column == 0) return current_row.get_white_move();
            else if (column == 1) return current_row.get_black_move();
            else return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row current_row;
            if(this.values.size() <= row){
                current_row = new Row();
                this.values.add(current_row);
            }
            else{current_row = this.values.get(row);}

            if (column == 0){
                current_row.set_white_move((String) aValue);
                fireTableRowsInserted(row, row);
            }
            else if (column==1){
                current_row.set_black_move((String) aValue);
                fireTableCellUpdated(row,column);
            }
        }
        @Override
        public Class<?> getColumnClass(int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row{
        private String white_move;
        private String black_move;

        Row(){

        }
        public String get_white_move(){return this.white_move;}
        public String get_black_move(){return this.black_move;}
        public void set_white_move(final String move){this.white_move = move;}
        public void set_black_move(final String move){this.black_move = move;}
    }

}
