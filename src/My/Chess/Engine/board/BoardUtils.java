package My.Chess.Engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardUtils {
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] THIRD_COLUMN = initColumn(2);
    public static final boolean[] FOURTH_COLUMN = initColumn(3);
    public static final boolean[] FIFTH_COLUMN = initColumn(4);
    public static final boolean[] SIXTH_COLUMN = initColumn(5);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final int num_tiles = 64;
    public static final int num_tiles_per_row = 8; //is first column only works for 64 board, change later.
    public static final String[] ALGEBRAIC_NOTATION = initialize_algebraic_notation();

    public static final Map<String, Integer> POSITION_TO_COORDINATE = initialize_position_to_coordinate_map();

    private BoardUtils(){
        throw new RuntimeException("Can not instantiate BoardUtils");
    }

    private static boolean [] initColumn(int columnNumber) {
        final boolean[] column = new boolean[64];
        do {
            column[columnNumber] = true;
            columnNumber += 8;
        } while(columnNumber < 64);
        return column;
    }

    private static String[] initialize_algebraic_notation() {
        String[] finalArray = new String[64];
        int[] numbers = {8, 7, 6, 5, 4, 3, 2, 1};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int count = 0;
        for(int x : numbers) {
            for (String y : letters) {
                finalArray[count] = y + x;
                count++;
            }
        }
        return finalArray;
    }
    private static Map<String, Integer> initialize_position_to_coordinate_map() {
        final Map<String, Integer> position_coordinate = new HashMap<>();
        for (int i = 0; i<64; i++){
            position_coordinate.put(ALGEBRAIC_NOTATION[i], i);
        }
        return Collections.unmodifiableMap(position_coordinate);
    }

    public static boolean is_valid_tile_coordinate(final int coordinate) {
        return coordinate >= 0 && coordinate <64;
    }

    public static boolean is_a_file(int position){
        return (position % 8 == 0);
    }
    public static boolean is_b_file(int position){
        return (position % 8 == 1);
    }
    public static boolean is_f_file(int position){
        return (position % 8 == 6);
    }
    public static boolean is_h_file(int position){
        return (position % 8 == 7);
    }

    // careful maybe switch maybe slow?

    public static boolean is_first_rank(int position){
        return position >= 56 && position <= 63;
    }
    public static boolean is_second_rank(int position){
        return position >= 48 && position <= 55;
    }
    public static boolean is_third_rank(int position){
        return position >= 40 && position <= 47;
    }
    public static boolean is_fourth_rank(int position){
        return position >= 32 && position <= 39;
    }
    public static boolean is_fifth_rank(int position){
        return position >= 24 && position <= 31;
    }
    public static boolean is_sixth_rank(int position){
        return position >= 16 && position <= 23;
    }
    public static boolean is_seventh_rank(int position){return position >= 8 && position <= 15;}
    public static boolean is_eighth_rank(int position){
        return position >= 0 && position <= 7;
    }


    public static int get_coordinate_at_position(String destination_coordinate) {
        return POSITION_TO_COORDINATE.get(destination_coordinate);
    }
    public static String get_position_at_coordinate(int coordinate){
        return ALGEBRAIC_NOTATION[coordinate];
    }
    public static boolean isThreatenedBoard(final Board board) {
        return board.white_player().is_in_check() || board.black_player().get_opponent().is_in_check();
    }
    public static boolean isGameOver(final Board board) {
        return board.white_player().is_in_checkmate() || board.black_player().is_in_checkmate() ||
                board.white_player().is_in_stalemate() || board.black_player().is_in_stalemate();
    }
}
