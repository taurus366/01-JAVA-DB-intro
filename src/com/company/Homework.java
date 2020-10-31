package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.*;
import java.util.Properties;

public class Homework {

    public static final String CONNECTION_STRING =
            "jdbc:mysql://localhost:3306/";

    public static final String MINION_TABLE_NAME =
            "minions_db";

    Connection connection;

    private BufferedReader reader;

    public Homework(){
        this.reader =  new BufferedReader
                (new InputStreamReader(System.in));
    }


    public void setConnectons(String user, String password) throws SQLException {

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        connection = DriverManager.getConnection(CONNECTION_STRING + MINION_TABLE_NAME, properties);



    }

    public void getVillainsNamesEx2() throws SQLException {


        String query = "select v.name, count(mv.minion_id) as count\n" +
                "from villains as v\n" +
                "join minions_villains mv on v.id = mv.villain_id\n" +
                "group by v.id\n" +
                "having count > 15\n" +
                "order by count desc ;";

        PreparedStatement statement =
                connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();


     while (resultSet.next()){
         System.out.printf("%s %d%n",
                 resultSet.getString("name"), resultSet.getInt("count"));


     }

    }

    public void getMinionsNamesEx3() throws IOException, SQLException {



        System.out.println("Enter villain id");

        int villainId = Integer.parseInt(reader.readLine());


        String villainName = getEntityNameById(villainId, "villains");

        if (villainName == null){
            System.out.printf("No villain with id %d", villainId );
            return;
        }


        System.out.printf("Villains: %s%n", villainName);

        String query = "SELECT m.name, m.age from minions as m\n" +
                "join minions_villains mv on m.id = mv.minion_id\n" +
                "where mv.villain_id = ?";

        PreparedStatement statement  = connection.prepareStatement(query);

        statement.setInt(1, villainId);

        ResultSet resultSet = statement.executeQuery();

        int count = 1;
        while (resultSet.next()){
            System.out.printf("%d. %s %d%n",count++, resultSet.getString("name"),
                    resultSet.getInt("age"));
        }
    }

    private String getEntityNameById(int entityId, String tableName) throws SQLException {

        String query = String.format("SELECT name FROM %s WHERE id= ?", tableName);



        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,entityId);

        ResultSet resultSet = statement.executeQuery();


        return resultSet.next() ? resultSet.getString("name") : null;
    }

    public void addMinionsEx4() throws IOException, SQLException {


        System.out.printf("Enter info: name, age, town name:");

        String [] minionInfo = reader.readLine().split("\\s+");

        String minionName = minionInfo[0];
        int age = Integer.parseInt(minionInfo[1]);
        String townName = minionInfo[2];



        int townId = getEntityIdByName(townName, "towns");

        if (townId < 0){
            insertEntityInTowns(townName);
        }



    }

    private void insertEntityInTowns(String townName) throws SQLException {
        String query = "INSERT INTO towns(name) values(?)";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, townName);
        statement.execute();
    }

    private int getEntityIdByName(String entityName, String tableName) throws SQLException {

        String query = String.format("SELECT id FROM %s WHERE name = ?", tableName);

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1,entityName);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getInt(1) : -1;
    }

    public void changeTownNameEx5() throws IOException, SQLException {

        System.out.println("Enter countryName");

        String countryName = reader.readLine();

        String query = "UPDATE towns SET name = UPPER(name) WHERE country = ?";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1,countryName);

        int townsAffected = statement.executeUpdate();

        if (townsAffected > 0) {
            System.out.printf("%d town names were affected.%n", townsAffected);
        }else {
            System.out.println("No town names were affected.");
        }
    }

    public void increaseMinionsAgeEx8() throws IOException, SQLException {

        System.out.println("Enter minionsId");

        String [] minionId = reader.readLine().split("\\s+");

        for (int i = 0; i <= minionId.length -1; i++) {

            int minId = Integer.parseInt(minionId[i]);

            String query = "update `minions`\n" +
                    "set `name` = LOWER(name) , age = age +1\n" +
                    "where id = ?";


            CallableStatement callableStatement = connection.prepareCall(query);

            callableStatement.setInt(1,minId);

            callableStatement.execute();


        }

        String query = "select name, age from minions";

        PreparedStatement statement  = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            System.out.printf("%s %d%n", resultSet.getString("name"),
                    resultSet.getInt("age"));
        }



    }



    public void increaseAgeStoredProcedureEx9() throws IOException, SQLException {
        System.out.println("Enter minionId");

        int minionID = Integer.parseInt(reader.readLine());

        String query = "CALL usp_get_older(?)";

        CallableStatement callableStatement = connection.prepareCall(query);

        callableStatement.setInt(1,minionID);

        callableStatement.execute();


        String queryTwo= "SELECT name , age FROM minions\n" +
                "WHERE id = ?";

        PreparedStatement statement  = connection.prepareStatement(queryTwo);

        statement.setInt(1, minionID);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.printf("%s %d", resultSet.getString("name"),
                    resultSet.getInt("age"));
        }

    }


    public void changeTownNameEx7() throws IOException, SQLException {

        int minCount = getMinionCount();

        for (int i = 0; i < minCount /2; i++) {
            printMinion(i+1);
            printMinion(minCount-i);

        }

    }

    private int getMinionCount() throws SQLException {
        int minCount =0;

        String query = "select id from minions\n" +
                "order by id desc\n" +
                "limit 1";

        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            minCount = resultSet.getInt("id");
        }

        return minCount;
    }

    private void printMinion(int minId) throws SQLException {

        String query= "SELECT name  FROM minions\n" +
                "WHERE id = ?";

        PreparedStatement statement  = connection.prepareStatement(query);

        statement.setInt(1, minId);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.printf("%s%n", resultSet.getString("name"));
        }

    }
}
