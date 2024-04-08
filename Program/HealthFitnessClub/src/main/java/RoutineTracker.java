import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class RoutineTracker extends Tracker {
    private final Hashtable<Integer, Routine> routines;

    RoutineTracker(int member_id)
    {
        super(member_id);
        routines = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    @Override
    protected void initialize()
    {
        try {
            // Initialize Routines
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Routine WHERE member_id=?");
            ps.setInt(1, member_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                routines.put(rs.getInt("routine_id"), new Routine(rs.getInt("routine_id"), rs.getString("name"),
                        rs.getString("description"), rs.getString("frequency")));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int choice = Console.getIntInput("Display routines (1), create routine (2), or delete routine (3) > ", 1, 3);
        if (choice == 1){ display(); }
        else if (choice == 2){ create(); }
        else { delete(); }
    }

    /**
     * Creates a new routine in the database and adds it to the hash table.
     */
    @Override
    protected void create()
    {
        int input = 0;
        while (input != 2){
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Routine (name, description, frequency, member_id) VALUES (?, ?, ?, ?) RETURNING routine_id");
                String name = Console.getStrInput("Name > ", Constants.MAX_OBJECT_NAME_LENGTH);
                String description = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
                String frequency = Console.getStrInput("Frequency > ", Constants.MAX_FREQUENCY_LENGTH);
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setString(3, frequency);
                ps.setInt(4, member_id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int createdId = rs.getInt(1);
                Console.print("Add exercises!");
                while (input != 2){
                    try {
                        PreparedStatement ps2 = con.prepareStatement("INSERT INTO Exercise (name, description, sets, repetitions, routine_id) VALUES (?, ?, ?, ?, ?)");
                        String eName = Console.getStrInput("Name > ", Constants.MAX_OBJECT_NAME_LENGTH);
                        String eDescription = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
                        int sets = Console.getIntInput("Sets > ");
                        int reps = Console.getIntInput("Repetitions > ");
                        ps2.setString(1, eName);
                        ps2.setString(2, eDescription);
                        ps2.setInt(3, sets);
                        ps2.setInt(4, reps);
                        ps2.setInt(5, createdId);
                        ps2.executeUpdate();
                        input = Console.getIntInput("Add another exercise (1) or finish (2) > ", 1, 2);
                    }
                    catch (SQLException e){
                        Console.print(e.getMessage());
                    }
                }
                routines.put(createdId, new Routine(createdId, name, description, frequency));
                return;
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }
            input = Console.getIntInput("Try again (1) or back (2) > ", 1, 2);
        }
    }

    /**
     * Prompt the user to enter the ID of the routine they with to delete
     * and deletes it from the database and removes it from the hash table.
     */
    @Override
    protected void delete()
    {
        if (routines.isEmpty()){
            Console.print("Nothing to delete.");
            return;
        }
        Console.print(summary());
        int key = Console.getIntInput("Enter ID of routine to delete > ");
        if (routines.containsKey(key)){
            routines.get(key).deleteFromDB();
            routines.remove(key);
        }
        else {
            Console.print("Routine with that ID not found.");
        }
    }

    /**
     * Displays all the routines in the hash table
     */
    @Override
    protected void display()
    {
        Console.print(full());
    }

    /**
     * Gives a string representation of all the routines in the hash
     * table with all the information.
     * @return string representation of the routines
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Routine> enumeration = routines.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a summarized string representation of the routines.
     * @return summarized string representation of the routines
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Routine> enumeration = routines.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }
}
