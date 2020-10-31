import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DiabloGames {
    public static void main(String[] args) {
        // 1. Read props from external property file
        Properties props = new Properties();
        String path = DiabloGames.class.getClassLoader().getResource("sql/jdbc.properties").getPath();
        System.out.printf("Resource path: %s%n",path);

        try {
            props.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: add meaningful default

        System.out.println(props);

        // 2. try with resources - Connection , PreparedStatement
        try (Connection con = DriverManager.getConnection(props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
             PreparedStatement ps = con.prepareStatement(
                     "SELECT users.id,first_name,last_name,COUNT(*) count FROM users " +
                             "JOIN users_games ug ON users.id = ug.user_id " +
                             "WHERE user_name = ?"
             );
        ){
                ps.setString(1,"Petya");
            ResultSet rs = ps.executeQuery();

            // 3. Print results
            while (rs.next()){
                System.out.printf("|  %10d | %-15.15s | %-15.15s | %d |\n",
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("count")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
