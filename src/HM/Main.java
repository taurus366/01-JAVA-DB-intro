package HM;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Start HERE !
        HomeWork homeWork = new HomeWork();
        homeWork.setConnection("localhost", "localhost");

        System.out.printf("[%d] Get Villains’ Names\n" +
                "[%d] Get Minion Names\n" +
                "[%d] Add Minion\n" +
                "[%d] Change Town Names Casing\n" +
                "[%d] Remove Villain\n" +
                "[%d] Print All Minion Names\n" +
                "[%d] Increase Minions Age\n" +
                "[%d] Increase Age Stored Procedure\n",
                2,3,4,5,6,7,8,9);
        System.out.println("Which Home Work number do you want to test?");

        switch (scanner.nextLine()) {
            case "2":
                homeWork.getVillainsName();
                break;
            case "3":
                homeWork.getMinionsNames();
                break;
            case "4":
                homeWork.addMinion();
                break;
            case "5":
                homeWork.changeTownNamesCasing();
                break;
            case "6":
                homeWork.removeVillain();
                break;
            case "7":
                homeWork.printAllMinionNames();
                break;
            case "8":
                homeWork.increaseMinionsAge();
                break;
            case "9":
                homeWork.increaseAgeStoredProcedure();
                break;
        }


    }
}
