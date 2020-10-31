import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener {
    private Cell[][] grid;
    private int gridSize;
    private int score;
    private boolean isInGame;
    private int[] bestScore;
    private Random rnd;

    //JButtons
    private JButton jButton_start;
    private JButton jButton_exit;

    //JComboBoxes
    private JComboBox<String> jComboBox_gridSize;

    //JButton handler
    private ActionHandler actionHandler;

    public GamePanel() {
        this.setLayout(null);
        isInGame = false;
        rnd = new Random();
        actionHandler = new ActionHandler();
        readFromAFile();
        setUpUI();
        setUIVisibility(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        this.setBackground(GameUtilities.clr_background);

        if (isInGame) drawGrid(g);
        else drawMenu(g);

        g.dispose();
    }

    private void setUpUI(){
        //Adding JButtons to the JPanel
        //Start JButton
        jButton_start = new JButton("     START", GameUtilities.sprites[0]);
        jButton_start.setBounds(10, 235, 430, 50);
        jButton_start.addActionListener(actionHandler);
        add(jButton_start);


        //Exit JButton
        jButton_exit = new JButton("       EXIT", GameUtilities.sprites[1]);
        jButton_exit.setBounds(10, 325, 430, 50);
        jButton_exit.addActionListener(actionHandler);
        add(jButton_exit);


        //Lap JComboBox
        jComboBox_gridSize = new JComboBox<String>();
        jComboBox_gridSize.addItem("3x3");
        jComboBox_gridSize.addItem("4x4");
        jComboBox_gridSize.addItem("5x5");
        jComboBox_gridSize.addItem("6x6");
        jComboBox_gridSize.addItem("7x7");
        jComboBox_gridSize.addItem("8x8");
        jComboBox_gridSize.addActionListener(actionHandler);
        jComboBox_gridSize.setSelectedIndex(1);
        jComboBox_gridSize.setBounds(180, 190, 100, 30);
        add(jComboBox_gridSize);
    }

    private void setUIVisibility(boolean isVisible){
        jButton_start.setVisible(isVisible);
        jButton_exit.setVisible(isVisible);
        jComboBox_gridSize.setVisible(isVisible);
    }

    private void initiateGrid() {
        score = 0;
        grid = new Cell[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                grid[row][col] = new Cell();
            }
        }

        createACell(2);
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(GameUtilities.clr_grid);
        gridSize = jComboBox_gridSize.getSelectedIndex()+3;

        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 36));
        g2d.setColor(GameUtilities.clr_grid);
        g2d.drawString("Жиырма қырық сегіз", 75, 60);
    }

    private void drawGrid(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(GameUtilities.clr_grid);

        int cellSize = (int) Math.floor(400 / (float) gridSize);
        int wallSize = (int)Math.floor(40/(float)gridSize);

        g2d.fillRect(0, 175, 450, 450);


        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int cellVal = grid[row][col].getValue();

                int colorCoeff = (cellVal > 0) ? (int) Math.round(Math.log(cellVal) / Math.log(2)) : 0;

                Color cellColor;
                if (colorCoeff == 0)
                    cellColor = GameUtilities.clr_cell[0];
                else cellColor = GameUtilities.clr_cell[colorCoeff];

                g2d.setColor(cellColor);

                g2d.fillRect(10 + row * (wallSize + cellSize), 185 + col * (wallSize + cellSize), cellSize, cellSize);

                if (colorCoeff == 0) continue;
                g2d.setFont(new Font("TimesRoman", Font.PLAIN, (36 * cellSize) / 100));
                g2d.setColor(GameUtilities.clr_grid);
                g2d.drawString(Integer.toString(cellVal), 10 + cellSize / 2 - colorCoeff * 2 + row * (wallSize + cellSize), 185 + (int) (cellSize / 1.8) + col * (wallSize + cellSize));
            }
        }
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 36));
        g2d.setColor(GameUtilities.clr_grid);

        if (bestScore[gridSize - 3] < score) bestScore[gridSize - 3] = score;
        g2d.drawString("Score: " + score, 0, 150);
        g2d.drawString("Best: " + bestScore[gridSize - 3], 270, 150);
        g2d.drawString("Жиырма қырық сегіз", 0, 60);
    }

    private boolean moveCells(int xDir, int yDir, int startPos) {
        int initialStartPos = startPos;
        boolean hasMoved = false, result;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize - 1; j++) {
                int row = startPos * Math.abs(xDir) + i * Math.abs(yDir),
                        col = i * Math.abs(xDir) + startPos * Math.abs(yDir);
                result = moveACell(xDir, yDir, row, col);
                hasMoved = result || hasMoved;
                startPos -= xDir + yDir;
            }
            startPos = initialStartPos;
        }
        clearMerges();
        if (hasMoved && hasEmptyCells()) createACell(1);
        if (!checkMoveAvailability()) {
            repaint();
            JOptionPane.showMessageDialog(this, "Game has ended");
            initiateGrid();
            repaint();
        }
        return hasMoved;
    }

    private boolean moveACell(int xDir, int yDir, int row, int col) {
        boolean hasMoved = false;
        if (grid[row][col].getValue() == 0) return false;
        while (true) {
            if (grid[row + xDir][col + yDir].getValue() == 0) {
                hasMoved = true;
                grid[row + xDir][col + yDir].setValue(grid[row][col]);
                grid[row][col].clearValue();
                row += xDir;
                col += yDir;
                if (row + xDir < 0 || row + xDir > gridSize - 1) return true;
                if (col + yDir < 0 || col + yDir > gridSize - 1) return true;
            } else if (grid[row][col].getValue() == grid[row + xDir][col + yDir].getValue()) {
                if (!grid[row + xDir][col + yDir].hasMerged()) {
                    hasMoved = true;
                    grid[row + xDir][col + yDir].merge();
                    grid[row][col].clearValue();
                    score += grid[row + xDir][col + yDir].getValue();
                } else {
                    return hasMoved;
                }
            } else return hasMoved;
        }
    }

    private void clearMerges() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.clearMerge();
            }
        }
    }

    private boolean checkMoveAvailability() {

        return hasEmptyCells() ||
                hasTwoAdjacentCells();
    }

    private boolean hasEmptyCells() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.getValue() == 0) return true;
            }
        }
        return false;
    }

    private boolean hasTwoAdjacentCells() {
        for (int row =0; row<gridSize; row++) {
            for (int col =0; col<gridSize; col++) {
                if (row-1>0) {
                    if (grid[row-1][col].getValue() == grid[row][col].getValue()) return true;
                }
                if (row+1<gridSize) {
                    if (grid[row+1][col].getValue() == grid[row][col].getValue()) return true;
                }
                if (col-1>0) {
                    if (grid[row][col-1].getValue() == grid[row][col].getValue()) return true;
                }
                if (col+1<gridSize) {
                    if (grid[row][col+1].getValue() == grid[row][col].getValue()) return true;
                }
            }
        }
        return false;
    }

    private void createACell(int amount) {
        for (int i = 0; i < amount; i++) {
            int row = rnd.nextInt(gridSize);
            int col = rnd.nextInt(gridSize);

            if (grid[row][col].getValue() != 0) {
                i--;
            } else {
                grid[row][col].setRandomValue();
            }
        }
    }

    private void saveToAFile() {
        if (score < bestScore[gridSize - 3]) return;
        try (PrintWriter out = new PrintWriter("bestscore.txt")) {
            for (Integer bs : bestScore)
                out.println(bs);
        } catch (FileNotFoundException ignored) {
        }
    }

    private void readFromAFile() {
        bestScore = new int[6];

        try (FileReader in = new FileReader("bestscore.txt")) {
            BufferedReader bufferedReader = new BufferedReader(in);
            String line;

            int counter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                bestScore[counter] = Integer.parseInt(line);
                counter++;
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT: //arrow left is pressed
                moveCells(-1, 0, 1);
                break;
            case KeyEvent.VK_RIGHT: //arrow right is pressed
                moveCells(1, 0, gridSize - 2);
                break;
            case KeyEvent.VK_UP: //arrow up is pressed
                moveCells(0, -1, 1);
                break;
            case KeyEvent.VK_DOWN: //arrow down is pressed
                moveCells(0, 1, gridSize - 2);
                break;
            case KeyEvent.VK_R:
                saveToAFile();
                initiateGrid();
                break;
            //To exit the game
            case KeyEvent.VK_ESCAPE: //Escape key is pressed

                if (isInGame) {
                    int response = JOptionPane.showConfirmDialog(
                            this, "Are you sure you want to exit to the menu?",
                            "Powers of Two", JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
                    saveToAFile();
                    if (response == 0) {
                        setUIVisibility(true);
                        isInGame = false;
                        repaint();
                    }
                } else {
                    int response = JOptionPane.showConfirmDialog(
                            this, "Are you sure you want to exit the game?",
                            "Powers of Two", JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
                    saveToAFile();
                    if (response == 0) {
                        System.exit(0);
                    }
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // inner class to handle action events from JButtons
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == jButton_start) {
                isInGame = true;
                gridSize = jComboBox_gridSize.getSelectedIndex()+3;
                setUIVisibility(false);
                initiateGrid();
                repaint();
            } else if (e.getSource() == jButton_exit) {
                saveToAFile();
                System.exit(0);
            }
        }
    }
}
