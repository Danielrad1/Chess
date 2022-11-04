package My.Chess.gui;

import My.Chess.Engine.Pieces.Piece;
import My.Chess.Engine.board.Move;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Taken_Pieces_Panel extends JPanel {
    private final JPanel north_panel;
    private final JPanel south_panel;
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOUR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
    public Taken_Pieces_Panel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOUR);
        setBorder(PANEL_BORDER);
        this.north_panel = new JPanel(new GridLayout(8,2));
        this.south_panel = new JPanel(new GridLayout(8,2));
        this.north_panel.setBackground(PANEL_COLOUR);
        this.south_panel.setBackground(PANEL_COLOUR);
        this.add(this.north_panel, BorderLayout.NORTH);
        this.add(this.south_panel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }
    public void redo(final Table.Move_Log move_log){
        this.south_panel.removeAll();
        this.north_panel.removeAll();

        final List<Piece> white_taken_pieces = new ArrayList<>();
        final List<Piece> black_taken_pieces = new ArrayList<>();

        for (final Move move: move_log.get_moves()){
            if(move.is_attack()){
                final Piece taken_piece =  move.get_attacked_piece();
                if (taken_piece.get_piece_colour().is_white()){
                    white_taken_pieces.add(taken_piece);
                }
                else {
                    black_taken_pieces.add(taken_piece);
                }
            }
        }
        Collections.sort(white_taken_pieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.get_piece_value(), o2.get_piece_value());
            }
        });
        Collections.sort(black_taken_pieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.get_piece_value(), o2.get_piece_value());
            }
        });
        for (final Piece taken_piece : white_taken_pieces){
            try{
                final BufferedImage image = ImageIO.read(new File("art/simple/"
                        + taken_piece.get_piece_colour().toString().substring(0, 1) + "" + taken_piece.toString()
                        + ".gif"));
                final ImageIcon ic = new ImageIcon(image);
                final JLabel image_label = new JLabel(new ImageIcon(ic.getImage().getScaledInstance(
                        ic.getIconWidth() - 15, ic.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.south_panel.add(image_label);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        for (final Piece taken_piece : black_taken_pieces){
            try{
                final BufferedImage image = ImageIO.read(new File("art/simple/"
                        + taken_piece.get_piece_colour().toString().substring(0, 1) + "" + taken_piece.toString()
                        + ".gif"));
                final ImageIcon ic = new ImageIcon(image);
                final JLabel image_label = new JLabel(new ImageIcon(ic.getImage().getScaledInstance(
                        ic.getIconWidth() - 15, ic.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.north_panel.add(image_label);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        validate();
    }
}
