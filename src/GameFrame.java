import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Powers of Two");
        setBounds(300, 200, 450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel();
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(gamePanel);
        add(gamePanel);
    }

    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        gameFrame.setVisible(true);
    }
}
