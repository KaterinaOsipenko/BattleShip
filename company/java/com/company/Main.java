package java.com.company;
import java.util.Scanner;

public class Main {
    final static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        //Приветствие игрока 1
        System.out.println("Hello, player 1. Please enter your nickname: ");
        String name1 = scanner.nextLine();
        Player player1 = new Player(name1);
        System.out.println("Welcome to the game, " + player1.getName());

        //Приветствие игрока 2
        System.out.println("Hello, player 2. Please enter your nickname: ");
        String name2 = scanner.nextLine();
        Player player2 = new Player(name2);
        System.out.println("Welcome to the game, " + player2.getName());

        //Создание игровых полей
        System.out.println(player1.getName() + ", your field!");
        Field field1 = new Field(name1);
        drawField(field1);

        System.out.println(player2.getName() + ", your field!");
        Field field2 = new Field(name2);
        drawField(field2);

        //Заполнение игровых полей
        fillField(field1, player1);
        fillField(field2, player2);
        System.out.println("You have filled your field. So...\nAre you ready?\n Let`s GO!\n");
        game(field1, field2);

    }

    public static void drawField(Field field) {
        drawField(field.getField());
    }


    public static void drawField(Cell[][] cells) { // старт игры, булевая переменная
        System.out.println("   0  1  2  3  4  5  6  7  8  9");
        for (int i = 0; i < cells.length; i++) {
            System.out.print(" " + i + " ");
            for (int j = 0; j < cells[i].length; j++) {
                    System.out.print(cells[i][j].printSymbol() + "  ");
            }
            System.out.println();
        }
    }


    public static void fillField(Field field, Player player) {
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
                    System.out.println("Set the " + k + "-deck ship. Remained: " + m);
                    System.out.print("Enter the x-cord:");
                    x = scanner.nextInt();
                    System.out.print("Enter the y-cord:");
                    y = scanner.nextInt();

                    if (!checkCoordinates(x, y)) {
                        System.out.println("Coordinates are incorrect");
                        continue;
                    }

                    System.out.println("Choose the direction:\n1 - horizontal | 2 - vertical"); // пользователь выбирает напправление
                    int direction = 0;

                    while (direction == 0 || direction > 2) {
                        System.out.println("Please, choose 1 or 2.");
                        direction = scanner.nextInt();
                    }
                    flagCell = placeShip(x, y, k, Direction.of(direction), field);
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


    private static boolean placeShip(int x, int y, int size, Direction direction, Field field) {
        Cell[][] cells = field.getField();
        Cell[][] clone = cells.clone();

        size = size - 1; // TODO: можно избавится от -1 только надо все бцдет внимательно подправить
        if (x + size > 9 || y + size > 9) {
            System.out.println("Out of range, try again");
            return false;
        }


        if (direction == Direction.VERTICAL) { // TODO: вынести дупликаты в отдельный метод
            if (y + size + 1 < 10 && cells[y][x].state != 0 || y - 1 >= 0 && cells[y][x].state != 0) {
                System.out.println("Current cells are unavailable, chose another");
                return false;
            }
            for (int i = y; i <= y + size; i++) {
                clone[i][x].makeBusy();
            }
            if (y + size + 1 < 10) {
                clone[y + size + 1][x].makeReserved();
                size++;
            }
            if (y - 1 >= 0) {
                clone[y - 1][x].makeReserved();
                y--;
            } else {
                size--;
            }
            if (x - 1 >= 0) {
                if (checkCells(x - 1, y, size, clone, false)) {
                    return false;
                }
            }
            if (x + 1 < 10) {
                if (checkCells(x + 1, y, size, clone, false)) {
                    return false;
                }
            }
        } else {
            if (x + size + 1 < 10 && cells[y][x].state != 0 || x - 1 >= 0 && cells[y][x].state != 0){
                System.out.println("Current cells are unavailable, chose another");
                return false;
            }
            for (int i = x; i <= x + size; i++) {
                clone[y][i].makeBusy();
            }
            if (x + size + 1 < 10) {
                clone[y][x + size + 1].makeReserved();
                size++;
            }
            if (x - 1 >= 0) {
                clone[y][x - 1].makeReserved();
                x++;
            } else {
                size--;
            }
            if (y - 1 >= 0) {
                if (checkCells(x, y - 1, size, clone, true)) {
                    return false;
                }
            }
            if (y + 1 < 10) {
                if (checkCells(x, y + 1, size, clone, true)) {
                    return false;
                }
            }
        }

        field.setField(clone);
        return true;
    }

    public static boolean checkCoordinates(int x, int y) {
        return x >= 0 && x <= 9 && y >= 0 && y <= 9;
    }

    private static boolean checkCells(int x, int y, int size, Cell[][] cells, boolean isHorizontal) {
        int variable = isHorizontal ? x : y;
        int constant = isHorizontal ? y : x;
        for (int i = variable; i <= variable + size + 1; i++) {
            Cell cell = isHorizontal ? cells[constant][i] : cells[i][constant];
            if (cell.state != 0) {
                System.out.println("Sorry not available field");
                return true;
            }
            cell.makeReserved();
        }
        return false;
    }

     private static void game(Field field1, Field field2) {
        Scanner scanner = new Scanner(System.in);
        int x, y;
        int counter;
        int damageCell;
        boolean flag;
        boolean checker;
        Field currentField = field2;
        Player currentPlayer = field1.getPlayer();

        do {
            do {
                System.out.print(currentPlayer.getName() + ", make a move.\nEnter the x-cord:");
                x = scanner.nextInt();
                System.out.print("Enter the y-cord:");
                y = scanner.nextInt();
                checker = checkCoordinates(x, y);
                if (!checker) System.out.println("Your coordinates are bound of available range. Enter again.");
            } while (!checker);

            counter = 0;
            damageCell = 0;

            if (currentField.getField()[x][y].state == 6) {
                if ((x == 0 || x - 1 == 0) && (y == 9 || y + 1 == 9)) {
                    for (int i = 0; i <= x + 1; i++) {
                        for (int j = y - 1; j <= 9; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if ((x == 9 || x + 1 == 9) && (y == 9 || y + 1 == 9)) {
                    for (int i = x - 1; i <= 9; i++) {
                        for (int j = y - 1; j <= 9; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if ((x == 9 || x + 1 == 9) && (y == 0 || y - 1 == 0)) {
                    for (int i = x - 1; i <= 9; i++) {
                        for (int j = 0; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if ((x == 0 || x - 1 == 0) && (y == 0 || y - 1 == 0)) {
                    for (int i = 0; i <= x + 1; i++) {
                        for (int j = 0; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if (x == 0 || x - 1 == 0) {
                    for (int i = 0; i <= x + 1; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if (x == 9 || x + 1 == 9) {
                    for (int i = x - 1; i <= 9; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if (y == 0 || y - 1 == 0) {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = 0; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else if (y == 9 || y + 1 == 9) {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = y - 1; j <= 9; j++) {
                            if (currentField.getField()[i][j].state == 6) {
                                counter++;
                            }
                        }
                    }
                } else {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (currentField.getField()[i][j].state == 6) { // добавить счётчик на количество
                                counter++;
                            }
                            if (currentField.getField()[i][j].state == 3) {
                                damageCell++;
                            }
                        }
                    }
                }
                if (counter > 1 && damageCell >= 1) {
                    currentField.getField()[x][y].makeDamage();
                    drawField(currentField);
                    System.out.println("Hit! You damaged the ship. Make your turn again.");
                } else if (counter > 1) { // только занятые, занятые + дамаж - это дамаж, только дамаж - то килл
                    currentField.getField()[x][y].makeDamage();
                    drawField(currentField);
                    System.out.println("Hit! You damaged the ship. Make your turn again.");
                } else if (counter == 1 || damageCell >= 1) {
                    currentField.getField()[x][y].makeBeat();
                    if (x - 4 < 0 && y - 4 < 0) {
                        for (int i = 0; i <= x + 4; i++) {
                            for (int j = 0; j <= y + 4; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (x - 4 < 0 && y + 4 > 9) {
                        for (int i = 0; i <= x + 4; i++) {
                            for (int j = y - 4; j <= 9 ; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (x + 4 > 9 && y - 4 < 0) {
                        for (int i = x - 4; i <= 9; i++) {
                            for (int j = 0; j <= y + 4; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (x + 4 > 9 && y + 4 > 9) {
                        for (int i = x - 4; i <= 9; i++) {
                            for (int j = y - 4; j <=9 ; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (x - 4 < 0) {
                        for (int i = 0; i <= x + 4; i++) {
                            for (int j = y - 4; j <= y + 4; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (x + 4 > 9) {
                        for (int i = x - 4; i <= 9; i++) {
                            for (int j = y - 4; j <= y + 4; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (y - 4 < 0) {
                        for (int i = x - 4; i <= x + 4; i++) {
                            for (int j = 0; j <= y + 4; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else if (y + 4 > 9) {
                        for (int i = x - 4; i <= x + 4; i++) {
                            for (int j = y - 4; j <= 9; j++) {
                                if (currentField.getField()[i][j].state == 3) {
                                    currentField.getField()[i][j].makeBeat();
                                }
                            }
                        }
                    } else {
                            for (int i = x - 4; i <= x + 4; i++) {
                                for (int j = y - 4; j <= y + 4; j++) {
                                    if (currentField.getField()[i][j].state == 3) {
                                        currentField.getField()[i][j].makeBeat();
                                    }
                                }
                            }
                    }
                    drawField(currentField);
                    System.out.println("You kill the ship! Make your turn again.");
                }
            } else {
                currentField.getField()[x][y].makeThrough();
                drawField(currentField);
                System.out.println("Miss! Your opponents turn!");
                    currentField = currentField == field2 ? field1 : field2;
                    currentPlayer = currentPlayer == field1.getPlayer() ? field2.getPlayer() : field1.getPlayer();
                }

                flag = victory(currentField.getField());

            }
            while (!flag) ;

            System.out.println(currentPlayer.getName() + ", you are winner! Congratulations!\nGAME OVER!");

        }




    public static boolean victory(Cell[][] cells) {
        boolean flag = true;
        for (Cell[] cell : cells) {
            for (Cell value : cell) {
                if (value.state == 6) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
    
    enum Direction {
        VERTICAL, HORIZONTAL;
        
        public static Direction of(int type) {
            return type == 2 ? VERTICAL : HORIZONTAL;
        }
    }
}






