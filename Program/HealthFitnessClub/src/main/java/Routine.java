import java.sql.*;
import java.util.ArrayList;

public class Routine {
    private final Connection con;
    private  final int id;
    private final String name;
    private final String description;
    private final String frequency;
    private final ArrayList<Exercise> exercises;

    Routine(int id, String name, String description, String frequency)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.exercises = new ArrayList<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    private void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Exercise WHERE routine_id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                exercises.add(new Exercise(rs.getString("name"), rs.getString("description"), rs.getInt("sets"),
                        rs.getInt("repetitions")));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes the routine from the database.
     */
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Routine WHERE routine_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }

    }

    /**
     * Gives a summarized string representation of the class.
     * @return summarized string representation of the class
     */
    public String summary()
    {
        return "Name: " + name + "(ID: " + id + ")\n";
    }

    /**
     * Gives a string representation of the class.
     * @return string representation of the class
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(
                "Name: " + name +
                "\nDescription: " + description +
                "\nFrequency: " + frequency +
                "\nExercises:\n");
        for (Exercise e : exercises){
            sb.append(e);
        }
        return sb.toString();
    }
}
