import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static String DB_URL = "jdbc:mysql://localhost:3306/soft_uni";
    public static void main(String[] args) {



//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            System.exit(0);
//        }
//        System.out.println("successfully");

        Properties props = new Properties();
        props.setProperty("user","localhost");
        props.setProperty("password","localhost");
        Connection con = null;
        try {
           con = DriverManager.getConnection(DB_URL,props);
            //DriverManager.getConnection(DB_URL,"localhost","localhost");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }

        try {
          PreparedStatement ps =  con.prepareStatement("SELECT * FROM employees WHERE salary > ?");

          ps.setDouble(1,40000);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("|  %10d | %-15.15s | %-15.15s | %10.2f |\n",
                    rs.getLong("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDouble("salary")
                    );
        }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
