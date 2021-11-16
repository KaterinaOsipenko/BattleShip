package java.com.company;

public class Field {

    public static final int size = 10;

    private Player player;
    private Cell[][] field = new Cell[size][size];

    public Field(String name) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = new Cell();
            }
        }

        player = new Player(name);
    }

    public Cell[][] getField() {

        return field;
    }

    public Player getPlayer() {
        return player;
    }

    public void setField(Cell[][] field) {
        this.field = field;
    }

}

