import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton class for database control
public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/HealthFitnessClub";
    private static final String USER = "postgres";
    private static final String PASSWORD = "tomasteixeira";
    private static Connection con;

    static
    {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            con.setAutoCommit(true);
        }
        catch (ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Getter for the database connection.
     * @return the database connection
     */
    public static Connection getConnection()
    {
        return con;
    }

}
