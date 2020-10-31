package com.company;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {

        Homework homework = new Homework();

        homework.setConnectons("localhost", "localhost");

        //homework.getVillainsNamesEx2();
        //homework.getMinionsNamesEx3();

       // * homework.addMinionsEx4();

        //homework.changeTownNameEx5();

        //homework.changeTownNameEx7();

          //homework.increaseMinionsAgeEx8();

        homework.increaseAgeStoredProcedureEx9();




    }
}
