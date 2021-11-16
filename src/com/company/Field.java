package com.company;

import static com.company.Main.checkNumber;

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

    public Field() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = new Cell();
            }
        }
    }

    public Cell[][] getField() {

        return field;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
       this.player = player;
    }

    public void setField(Cell[][] field) {
        this.field = field;
    }

    protected static void drawField(Field field) {
        drawField(field.getField());
    }


    protected static void drawField(Cell[][] cells) { // старт игры, булевая переменная
        System.out.println("   0  1  2  3  4  5  6  7  8  9");
        for (int i = 0; i < cells.length; i++) {
            System.out.print(" " + i + " ");
            for (int j = 0; j < cells[i].length; j++) {
                System.out.print(cells[i][j].printSymbol() + "  ");
            }
            System.out.println();
        }
    }

    protected static void randomFillField(Field field, boolean bot_in_game) {
        Cell[][] cells = field.getField();
        for (Cell[] cell : cells) {
            for (Cell value : cell) { // //делаем ввсе клетки свободными
                value.makeFree();
            }
        }
        for (int k = 1; k <= 4; k++) {  // количество палуб у корабля
            for (int m = 5 - k; m >= 1; m--) { // количество оставшихся кораблей
                int x;
                int y;
                boolean flagCell;
                int direction;

                do {
                    x = (int) (Math.random() * 10);
                    y = (int) (Math.random() * 10);
                    direction = 1 + (int)(Math.random() * 2);
                    flagCell = placeShip(x, y, k,  Main.Direction.of(direction), field, bot_in_game);
                } while (!flagCell);
            }
        }
        drawField(field);
        for (Cell[] cell : cells) {
            for (Cell value : cell) {
                if (value.state == 1) {
                    value.makeBusyInGame();
                } else if (value.state == 5) {
                    value.makeReservedInGame();
                }
            }
        }
    }

    protected static void fillField(Field field, Player player, boolean bot_in_game) {
        Cell[][] cells = field.getField();
        for (Cell[] cell : cells) {
            for (Cell value : cell) { // //делаем ввсе клетки свободными
                value.makeFree();
            }

        }
        System.out.println(player.getName() + ", please fill your field!");
        for (int k = 1; k <= 4; k++) {  // количество палуб у корабля
            for (int m = 5 - k; m >= 1; m--) {  // количество оставшихся кораблей
                int x;
                int y;
                boolean flagCell = false;


                do {
                    int direction = 0;
                    System.out.println("Set the " + k + "-deck ship. Remained: " + m);
                    System.out.print("Enter the x-cord:");
                    x = checkNumber();
                    System.out.print("Enter the y-cord:");
                    y = checkNumber();
                    if (!checkCoordinates(y, x)) {
                        System.out.println("Coordinates are incorrect");
                        continue;
                    }
                    if (k != 1) {
                        System.out.println("Choose the direction:\n1 - horizontal | 2 - vertical"); // пользователь выбирает напправление


                        while (direction == 0 || direction > 2) {

                            System.out.println("Please, choose 1 or 2.");
                            direction = checkNumber();
                        }
                    } else {
                        direction = 1;
                    }
                    flagCell = placeShip(x, y, k, Main.Direction.of(direction), field, bot_in_game);

                } while (!flagCell);

                drawField(field.getField()); //выводим поле после расставления каждого корабля
            }
        }
        for (Cell[] cell : cells) {
            for (Cell value : cell) {
                if (value.state == 1) {
                    value.makeBusyInGame();
                } else if (value.state == 5) {
                    value.makeReservedInGame();
                }
            }
        }
    }

    private static boolean positionShip(int x, int y, int size, Cell[][] cells, boolean isHorizontal, boolean bot_in_game) {
        Cell[][] clone = cells.clone();
        int main = isHorizontal ? x : y;
        int side = isHorizontal ? y : x;
        if (!isHorizontal) {
            if (main + size > 10 || cells[main][side].state == 1 || cells[main][side].state == 5) {
                if (!bot_in_game) System.out.println("Current cells are unavailable, chose another");
                return true;
            }
        } else {
            if (main + size > 10 || cells[side][main].state == 1 || cells[side][main].state == 5) {
                if (!bot_in_game) System.out.println("Current cells are unavailable, chose another");
                return true;
            }
        }
        for (int i = main; i < main + size; i++) {
            if (!isHorizontal) {
                if (clone[main + size - 1][side].state == 5 || clone[main + size - 1][side].state == 1) {
                    if (!bot_in_game) System.out.println("This cell is busy");
                    return true;
                }
                clone[i][side].makeBusy();
            } else {
                if (clone[side][main + size - 1].state == 5 || clone[side][main + size - 1].state == 1) {
                    if (!bot_in_game) System.out.println("This cell is busy");
                    return true;
                }
                clone[side][i].makeBusy();
            }
        }
        if (main + size + 1 <= 10) {
            if (!isHorizontal) {
                clone[main + size][side].makeReserved();
            } else {
                clone[side][main + size].makeReserved();
            }
            size++;
        }
        if (main - 1 >= 0) {
            if (!isHorizontal) {
                clone[main - 1][side].makeReserved();
            } else {
                clone[side][main - 1].makeReserved();
            }
            main--;
        } else {
            size--;
        }
        if (side - 1 >= 0) {
            if (!isHorizontal) {
                if (checkCells(side - 1, main, size, clone, false)) {
                    return true;
                }
            } else {
                if (checkCells(main, side - 1, size, clone, true)) {
                    return true;
                }
            }
        }
        if (side + 1 < 10) {
            if(!isHorizontal) {
                if(checkCells(side + 1, main, size, clone, false)) {
                    return true;
                }
            } else {
                if (checkCells(main, side + 1, size, clone, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean placeShip(int x, int y, int size, Main.Direction direction, Field field, boolean bot_in_game) {
        Cell[][] cells = field.getField();
        Cell[][] clone = cells.clone();
        boolean pos;
        pos = positionShip(x, y, size, clone, direction != Main.Direction.VERTICAL, bot_in_game);
        if (pos) {
            return false;
        } else {
            field.setField(clone);
            return true;
        }
    }

    public static boolean checkCoordinates(int y, int x) {
        return x >= 0 && x <= 9 && y >= 0 && y <= 9;
    }

    private static boolean checkCells(int x, int y, int size, Cell[][] cells, boolean isHorizontal) {
        int variable = isHorizontal ? x : y;
        int constant = isHorizontal ? y : x;
        for (int i = variable; i <= variable + size; i++) {
            Cell cell = isHorizontal ? cells[constant][i] : cells[i][constant];
            if (cell.state == 1) {
                System.out.println("Sorry not available field");
                return true;
            }
            cell.makeReserved();
        }
        return false;
    }

}

