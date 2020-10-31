
import java.util.Random;

public class Cell {
    private int value;
    private boolean merged;

    public Cell() {
        value = 0;
        merged = false;
    }

    public int getValue() {
        return value;
    }

    public void setRandomValue() {
        Random rnd = new Random();
        value = (rnd.nextInt(101) > 20) ? 2 : 4;
    }

    public void setValue(Cell cell) {
        value = cell.getValue();
    }

    public void merge() {
        value *= 2;
        merged = true;
    }

    public void clearValue() {
        value = 0;
    }

    public boolean hasMerged() {
        return merged;
    }

    public void clearMerge() {
        merged = false;
    }
}
