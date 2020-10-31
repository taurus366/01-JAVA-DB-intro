package HM;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class HomeWork {
    private Connection connection;
    Scanner scanner = new Scanner(System.in);
    ResultSet rs;
    private static final String DB_CONNECTION_STRING = "jdbc:mysql://localhost:3306/";
    private static final String SCHEMA_STRING = "minions_db";

    public void setConnection(String user, String password) {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        try {
            this.connection = DriverManager.getConnection(DB_CONNECTION_STRING + SCHEMA_STRING, properties);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    public void getVillainsName() {
        String sql = "SELECT v.`name` AS `name` , COUNT(*) AS `count` " +
                "FROM `villains` AS v " +
                "JOIN `minions_villains` AS mv " +
                "ON mv.`villain_id` = v.`id` " +
                "JOIN `minions` AS m ON " +
                "m.`id` = mv.`villain_id` " +
                "GROUP BY m.`name` " +
                "HAVING `count` > ? " +
                "ORDER BY `count` DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, 15);

            rs = ps.executeQuery();

            while (rs.next()) {

                System.out.printf("%s %d\n",
                        rs.getString("name"),
                        rs.getInt("count"));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getMinionsNames() {
        String sql = "SELECT v.`name` AS g, m.`name` , m.`age`\n" +
                "FROM `minions` AS m\n" +
                "JOIN `minions_villains` AS mv\n" +
                "ON mv.`minion_id` = m.`id`\n" +
                "JOIN `villains` AS v\n" +
                "ON v.`id` = mv.`villain_id`\n" +
                "WHERE v.`id` = ?";

        System.out.println("Write a villain ID for print on the console all minion names and their age.");
        int villainID = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, villainID);

            rs = ps.executeQuery();

            int count = 0;
            if (!rs.next()) {
                System.out.printf("No villain with ID %d exists in the database.", villainID);
                System.exit(0);
            } else {
                rs = ps.executeQuery();
                while (rs.next()) {
                    count++;
                    if (count == 1) {
                        System.out.println("Villain: " + rs.getString(1));
                    }

                    System.out.printf("%d. %s %d\n",
                            count,
                            rs.getString("name"),
                            rs.getInt("age"));
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void changeTownNamesCasing() {
        String sql = "SELECT `name` , `country`\n" +
                "FROM `towns`\n" +
                "WHERE `country` = ?";

        String sqlUpdate = "UPDATE `towns`\n" +
                "SET `name` = upper(`name`)\n" +
                "WHERE `country` = ?";

        System.out.println("Please write country name:");

        String country = scanner.nextLine();

        ArrayList<String> towns = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, country);

            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("No town names were affected.");
                System.exit(0);
            } else {
                rs = ps.executeQuery();
                while (rs.next()) {
                    towns.add(rs.getString("name").toUpperCase());

                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setString(1, country);

            ps.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        System.out.printf("%d town names were affected.\n", towns.size());
        System.out.printf("%s", towns.toString());


    }

    public void increaseMinionsAge() {
        String sqlSelect = "SELECT `name` , `age` FROM `minions`";

        String sql = "UPDATE `minions`\n" +
                "SET `name` = LOWER(`name`),\n" +
                "`age` = `age` + 1\n" +
                "WHERE `id` IN (";

        String temp = "";
        System.out.println("Write minion's ID");
        int[] minionsID = Arrays.stream(scanner.nextLine().split("\\s+"))
                .mapToInt(Integer::parseInt).toArray();


        for (int i = 0; i < minionsID.length; i++) {
            temp += ",?";
        }

        temp = temp.replaceFirst(",", "");
        temp += ")";
        sql = sql + temp;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < minionsID.length; i++) {
                ps.setInt(i + 1, minionsID[i]);
            }

            ps.executeUpdate();
            ps.clearParameters();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlSelect)) {
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("%s %d\n",
                        rs.getString("name"),
                        rs.getInt("age")
                );

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void removeVillain() {
        String sqlSelect = "SELECT v.`name` , COUNT(m.`id`) AS `count`\n" +
                "FROM `villains` AS v\n" +
                "JOIN `minions_villains` AS mv\n" +
                "ON mv.`villain_id` = v.`id`\n" +
                "JOIN `minions` AS m\n" +
                "ON m.`id` = mv.`minion_id`\n" +
                "WHERE v.`id` = ?\n";

        int minionsCount = 0;
        String villianName = "";

        System.out.println("Please write the ID of the Villain:");

        int villainsID = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement ps = connection.prepareStatement(sqlSelect)) {
            ps.setInt(1, villainsID);
            rs = ps.executeQuery();


            while (rs.next()) {
                minionsCount = rs.getInt("count");
                villianName = rs.getString("name");

            }
            if (minionsCount == 0) {
                System.out.println("No such villain was found");
                System.exit(0);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String sqlDelete1 = "DELETE FROM `minions_villains` \n" +
                "WHERE `villain_id` = ?";

        String sqlDelete2 = "DELETE FROM `villains` " +
                "WHERE `id` = ?";

        try (PreparedStatement ps1 = connection.prepareStatement(sqlDelete1);
             PreparedStatement ps2 = connection.prepareStatement(sqlDelete2)) {
            ps1.setInt(1, villainsID);
            ps1.executeUpdate();

            ps2.setInt(1, villainsID);
            ps2.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        System.out.printf("%s was deleted\n%d minions released", villianName, minionsCount);


    }

    public void increaseAgeStoredProcedure() {
        String sql = "CREATE PROCEDURE usp_get_older(minion_id INT)\n" +
                "BEGIN\n" +
                "UPDATE `minions`\n" +
                "SET `age` = `age` + 1\n" +
                "WHERE `id` = minion_id;\n" +
                "SELECT `name` , `age`\n" +
                "FROM `minions`\n" +
                "WHERE `id` = minion_id;\n" +
                "END";

        String sqlSelect = "CALL usp_get_older(?)";

        System.out.println("Please write minion's ID:");
        int minionID = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.executeUpdate();


        } catch (SQLSyntaxErrorException ignored) {

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (PreparedStatement ps1 = connection.prepareStatement(sqlSelect)) {
            ps1.setInt(1, minionID);
            rs = ps1.executeQuery();

            while (rs.next()) {
                System.out.printf("%s %d\n",
                        rs.getString("name"),
                        rs.getInt("age"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void printAllMinionNames() {
        String sql = "SELECT * FROM `minions`";

        ArrayList<String> minionsList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            rs = ps.executeQuery();

            while (rs.next()) {
                minionsList.add(rs.getString("name"));
            }

            for (int i = 0; i < minionsList.size() / 2; i++) {
                System.out.println(minionsList.get(i));
                System.out.println(minionsList.get((minionsList.size() - 1) - i));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addMinion() {

        String sqlSelectTown = "SELECT * FROM `towns` WHERE `name` = ?";
        String sqlInserTown = "INSERT INTO `towns` (`name`,`country`) " +
                "VALUES(?,'random')";
        String sqlSelectVillain = "SELECT * FROM `villains` WHERE `name` = ?";
        String sqlInsertVillain = "INSERT INTO `villains` (`name`,`evilness_factor`) " +
                "VALUES(?,'evil')";
        String sqlSelectMinion = "SELECT * FROM `minions` WHERE `name` = ?";
        String sqlInsertMinion = "INSERT INTO `minions` (name, age, town_id) " +
                "VALUES(?,?,?)";
        String sqlInsertMinionVillian = "INSERT INTO `minions_villains` (`minion_id`,`villain_id`) " +
                "VALUES(?,?)";

        System.out.println("Please write minion name , age , town !EXAMPLE : -> [Minion: Robert 14 Berlin]");
        String[] inputMinion = scanner.nextLine().split("\\s+");
        System.out.println("Please write villain name !EXAMPLE : -> [Villain: Jimmy]");
        String[] inputVillain = scanner.nextLine().split("\\s+");

        String minionName = inputMinion[1];
        int minionAge = Integer.parseInt(inputMinion[2]);
        String town = inputMinion[3];
        String villainName = inputVillain[1];
        int townID = 0;
        int villainID = 0;
        int minionID = 0;


        try (PreparedStatement ps = connection.prepareStatement(sqlSelectTown)) {
            ps.setString(1, town);
            rs = ps.executeQuery();
            if (!rs.next()) {
                try (PreparedStatement psInsert = connection.prepareStatement(sqlInserTown)) {
                    psInsert.setString(1, town);
                    psInsert.executeUpdate();
                    System.out.printf("Town %s was added to the database.\n", town);
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                townID = rs.getInt("id");
            }

            try (PreparedStatement ps1 = connection.prepareStatement(sqlSelectMinion)) {
                ps1.setString(1, minionName);
                rs = ps1.executeQuery();
                if (!rs.next()) {
                    try (PreparedStatement psInsertMinion = connection.prepareStatement(sqlInsertMinion)) {
                        psInsertMinion.setString(1, minionName);
                        psInsertMinion.setInt(2, minionAge);
                        psInsertMinion.setInt(3, townID);
                        psInsertMinion.executeUpdate();

                    }
                }
                rs = ps1.executeQuery();
                while (rs.next()) {
                    minionID = rs.getInt("id");
                }
            }


            try (PreparedStatement ps2 = connection.prepareStatement(sqlSelectVillain)) {
                ps2.setString(1, villainName);
                rs = ps2.executeQuery();

                if (!rs.next()) {
                    try (PreparedStatement psInsertVillain = connection.prepareStatement(sqlInsertVillain)) {
                        psInsertVillain.setString(1, villainName);
                        psInsertVillain.executeUpdate();
                        System.out.printf("Villain %s was added to the database.\n", villainName);
                    }
                }
                rs = ps2.executeQuery();
                while (rs.next()) {
                    villainID = rs.getInt("id");
                }
            }

            try (PreparedStatement ps3 = connection.prepareStatement(sqlInsertMinionVillian)) {
                ps3.setInt(1, minionID);
                ps3.setInt(2, villainID);
                ps3.executeUpdate();
                System.out.printf("Successfully added %s to be minion of %s.", minionName, villainName);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
