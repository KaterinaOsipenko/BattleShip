package java.com.company;
import java.util.Scanner;

public class Cell {
    int state = 0; // 0 1 2 3 4

    public String printSymbol() {
        switch (state) {
            case 1: //клетка занята
                return "S";
            case 2: //клетка промазана
                return "\u2022";
            case 3: //клетка ранена
                return "*";
            case 4: //клетка убита
                return "#";
            case 5:
                return "~";
            case 6:
                return "_";
            case 7:
                return "_";
            case 0: //клетка свободна
                return "_";
            default:
                return "=";
        }
    }

    public void makeDamage() {
        this.state = 3;
    }

    public void makeBeat() {
        this.state = 4;
    }

    public void makeBusy() {

        this.state = 1;
    }

    public void makeFree() {

        this.state = 0;
    }

    public void makeThrough() {

        this.state = 2;
    }

    public void makeReserved() {
        this.state = 5;
    }

    public void makeBusyInGame() {
        this.state = 6;
    }

    public void makeReservedInGame() {
        this.state = 7;
    }
}
