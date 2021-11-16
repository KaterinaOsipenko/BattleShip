package com.company;
import java.util.Scanner;

public class Main {
    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.print("Welcome to the BattleShip! Chose game mode:\nIf you would like to play with computer press '1'" +
                "\nIf you would like to play with player press '2'\nYour chose: ");

        int mode_of_enemy = checkNumber();
        Field field = new Field();
        Field field1 = new Field();
        Field field2 = new Field();
        Field field_bot = new Field();

        switch (mode_of_enemy) {
            case 1:
                System.out.println("Hello, player 1. Please enter your nickname: ");
                String name = scanner.nextLine();
                Player player = new Player(name);
                field.setPlayer(player);
                System.out.println("Welcome to the game, " + player.getName());

                System.out.println(player.getName() + ", your field!");
                Field.drawField(field);

                mode_of_filling(field, player);
                Player bot = new Player();
                field_bot.setPlayer(bot);
                Field.randomFillField(field_bot, true);
                //drawField(field_bot);
                System.out.println("You have filled your field. So...\nAre you ready?\n Let`s GO!\n");
                game(field1, field2, field, field_bot, true);
                break;
            case 2:
                //Приветствие игрока 1
                System.out.println("Hello, player 1. Please enter your nickname: ");
                String name1 = scanner.nextLine();
                Player player1 = new Player(name1);
                field1.setPlayer(player1);
                System.out.println("Welcome to the game, " + player1.getName());

                //Приветствие игрока 2
                System.out.println("Hello, player 2. Please enter your nickname: ");
                String name2 = scanner.nextLine();
                Player player2 = new Player(name2);
                field2.setPlayer(player2);
                System.out.println("Welcome to the game, " + player2.getName());

                //Создание игровых полей
                System.out.println(player1.getName() + ", your field!");
                Field.drawField(field1);

                System.out.println(player2.getName() + ", your field!");

                Field.drawField(field2);

                //Заполнение игровых полей

                mode_of_filling(field1, player1);
                mode_of_filling(field2, player2);

                System.out.println("You have filled your field. So...\nAre you ready?\n Let`s GO!\n");
                game(field1, field2, field, field_bot, false);
                break;
            default:
                System.out.println("Wrong instruction.");
                break;
        }

    }

    private static void mode_of_filling(Field field, Player player) {
        System.out.print(player.getName() + ", chose the mode of filling your field.\nIf you would like to choose manual" +
                " input press '1'\nIf you would like to choose random input press '2'\nYour chose: ");
        int mode_of_filling = checkNumber();
        switch (mode_of_filling) {
            case 1:
                Field.fillField(field, player, false);
                break;
            case 2:
                Field.randomFillField(field, true);
                break;
            default:
                System.out.println("Wrong instruction.");
                break;
        }
    }

    private static void game(Field field1, Field field2, Field field, Field field_bot, boolean bot_in_game) {
        int x = 0, y = 0;
        int counter;
        boolean flag;
        boolean checker;
        boolean botShot = false;
        Field currentField = bot_in_game ? field_bot : field2;
        Player currentPlayer = bot_in_game ? field.getPlayer() : field1.getPlayer();

        do {
            do {
                if (currentPlayer == field_bot.getPlayer()) {
                    if(botShot) {
                        if (x == 9) {
                            y++;
                        } else {
                            x++;
                        }
                    } else {
                        x = (int) (Math.random() * 10);
                        y = (int) (Math.random() * 10);
                    }
                } else {
                    System.out.print(currentPlayer.getName() + ", make a move.\nEnter the x-cord:");
                    x = checkNumber();
                    System.out.print("Enter the y-cord:");
                    y = checkNumber();
                }
                checker = Field.checkCoordinates(y, x);
                if (!checker)
                    System.out.println("Your coordinates are bound of available range. Enter again.");

            } while (!checker);

            if (currentField.getField()[y][x].state != 6) {
                currentField.getField()[y][x].makeThrough();
                Field.drawField(currentField);
                if(currentPlayer == field_bot.getPlayer()) {
                    System.out.println("Bot made a bad shot.");
                } else {
                    System.out.println("Miss! Your opponents turn!");
                }
                if (bot_in_game) {
                    currentField = currentField == field_bot ? field : field_bot;
                    currentPlayer = currentPlayer == field.getPlayer() ? field_bot.getPlayer() : field.getPlayer();
                } else {
                    currentField = currentField == field2 ? field1 : field2;
                    currentPlayer = currentPlayer == field1.getPlayer() ? field2.getPlayer() : field1.getPlayer();
                }
            } else {
                counter = checkHit(currentField, y, x);
                if (counter > 1 ) {
                    currentField.getField()[y][x].makeDamage();
                    System.out.println();
                    Field.drawField(currentField);
                    if(currentPlayer == field_bot.getPlayer()) {
                        System.out.println("Bot hurt your ship!");
                        botShot = true;
                    } else {
                        System.out.println("Hit! You damaged the ship. Make your turn again.");
                    }
                } else if (counter == 1) {
                    currentField.getField()[y][x].makeBeat();
                    makeBeat(currentField, y, x);
                    System.out.println();
                    Field.drawField(currentField);
                    if(currentPlayer == field_bot.getPlayer()) {
                        System.out.println("Bot killed your ship!");
                    } else {
                        System.out.println("You killed the ship! Make your turn again.");
                    }
                }
            }
            flag = victory(currentField.getField());
        }
        while (!flag);
        if(currentPlayer == field_bot.getPlayer()) {
            System.out.println("Bot won. You, unfortunately, lost!\nGAME OVER!");
        } else {
            System.out.println(currentPlayer.getName() + ", you are winner! Congratulations!\nGAME OVER!");
        }

    }


    private static void makeBeat (Field field, int y, int x) {
        int i1 = Math.max(x - 4, 0);
        int i2 = Math.min(x + 4, 9);
        int j1 = Math.max(y - 4, 0);
        int j2 = Math.min(y + 4, 9);
        for (int i = i1; i <= i2; i++) {
            for (int j = j1; j <= j2; j++) {
                if (field.getField()[j][i].state == 3) field.getField()[j][i].makeBeat();
            }
        }
    }

    private static int checkHit (Field field, int y, int x) {
        int counter = 0;
        int i1 = x == 0 || x - 1 == 0 ? 0 : x - 1;
        int i2 = x == 9 || x + 1 == 9 ? 9 : x + 1;
        int j1 = y == 0 || y - 1 == 0 ? 0 : y - 1;
        int j2 = y == 9 || y + 1 == 9 ? 9 : y + 1;
        for (int i = i1; i <= i2; i++) {
            for (int j = j1; j <= j2; j++) {
                if (field.getField()[j][i].state == 6) counter++;
            }
        }
        return counter;
    }

    private static boolean victory(Cell[][] cells) {
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

    public static int checkNumber() {
        int number;
        while (true) {
            try {
                number = Integer.parseInt(new Scanner(System.in).next());
                break;
            } catch (NumberFormatException en) {
                System.out.println("Please, enter the number.");
            }
        }
        return number;
    }

    enum Direction {
        VERTICAL, HORIZONTAL;

        public static Direction of(int type) {
            return type == 2 ? VERTICAL : HORIZONTAL;
        }
    }


}




/* 1) Поменяла все проверки с != 0 на == 1 (на этих проверках мы резервируем клеточки, если клеточка уже зарезервирована, то мы можем
наложить на неё ещё резерв. Стоит проверять или клеточка не занята)
    2) Убрала проверку Out of range и добавила его в зависимоти от направления в проверку сразу после if (direction == ...)
    3) Добавила проверку на запрос направления (если корабль однопалубный, то не спрашивает, а по умолчанию горизонтальное)
    4) Избавилась от size = size - 1
    5) Добавила проверку на makeBusy ибо корабли могли накладываться друг на друга
    6) placeShips в одном методе
    7) Добавила проверку на вводимые значения
    8) Сократила game как смогла
    9) Добавила меню на режим игры
    10) Добавила метод рандомной расстановки кораблей
    11) Добавила возможность на рандомную расстановку кораблей пользователям
    12) Добавила возможность игры с ботом
    13) Во время игры, если бот ранил, он проверяет клеточку, что больше на 1 по х или у
 */

