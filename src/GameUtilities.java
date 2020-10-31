import javax.swing.*;
import java.awt.*;

public class GameUtilities {
    //Colors
    public static Color clr_background = new Color(25, 66, 110);
    public static Color clr_grid = new Color(153, 148, 170);
    public static Color[] clr_cell = new Color[]{
            new Color(104, 155, 155), // Empty
            new Color(69, 114, 147), // 2
            new Color(25, 53, 122), // 4
            new Color(4, 54, 84), // 8
            new Color(17, 17, 80), // 16
            new Color(116, 161, 97), // 32
            new Color(44, 161, 19), // 64
            new Color(35, 100, 52), //128
            new Color(50, 68, 62), //256
            new Color(248, 19, 18), //512
            new Color(203, 47, 57), //1024
            new Color(172, 18, 20), //2048

    };

    //Images
    public static ImageIcon[] sprites = new ImageIcon[]{
            new ImageIcon("sprites/startIcon.png"), //start icon
            new ImageIcon("sprites/exitIcon.png") //exit icon
    };
}
