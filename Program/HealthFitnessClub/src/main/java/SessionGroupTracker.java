import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class SessionGroupTracker extends Tracker {
    private final Constants.UserType userType;
    private final Hashtable<Integer, SessionGroup> sessions;

    SessionGroupTracker(int member_id, Constants.UserType userType)
    {
        super(member_id);
        this.userType = userType;
        sessions = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    @Override
    protected void initialize()
    {
        String sql = """
                SELECT
                s.g_session_id,
                s.name,
                s.description,
                s.price,
                s.moment,
                s.capacity,
                CONCAT(t.first_name, ' ', t.last_name) AS trainer_name,
                r.number
                FROM GroupSession s\s
                JOIN Trainer t ON s.trainer_id = t.id
                JOIN Room r ON s.room_id = r.room_id
                """;

        PreparedStatement ps;
        try {
            if (userType == Constants.UserType.Trainer){
                ps = con.prepareStatement(sql + " WHERE trainer_id=?");
                ps.setInt(1, member_id);
            }
            else if (userType == Constants.UserType.Member){
                ps = con.prepareStatement(sql +
                        " JOIN Participants p ON s.g_session_id=p.g_session_id\n" +
                        "WHERE member_id=?");
                ps.setInt(1, member_id);
            }
            else {
                ps = con.prepareStatement(sql);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                sessions.put(rs.getInt("g_session_id"), new SessionGroup(rs.getInt("g_session_id"), rs.getString("name"), rs.getString("description"),
                        rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"), rs.getString("number"), rs.getInt("capacity")));
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
        int choice;
        if (userType == Constants.UserType.Member){
            choice = Console.getIntInput("Display my group sessions (1), cancel a group session (2), register for a group session (3) > ", 1, 3);
            if (choice == 3){ choice = 4; }
        }
        else if (userType == Constants.UserType.Trainer){
            choice = Console.getIntInput("Display my group sessions (1), cancel a group session (2), create a group session (3) > ", 1, 3);
        }
        else {
            choice = Console.getIntInput("Display all group sessions (1), cancel a group session (2), create a group session (3) > ", 1, 3);
        }
        if (choice == 1){ display(); }
        else if (choice == 2){ delete(); }
        else if (choice == 4){ register(); }
        else { create(); }
    }

    /**
     * List all group sessions then prompt the user to select the
     * ID of the group session they wish to register for then add
     * them to the participation list in the database.
     */
    private void register()
    {
        try {
            Console.print("Available Group Sessions:");
            PreparedStatement ps = con.prepareStatement("""
                        SELECT s.g_session_id, s.name, s.description, s.price,
                        CONCAT(t.first_name, ' ', t.last_name) AS full_name
                        FROM GroupSession s JOIN Trainer t ON s.trainer_id=t.id
                        LEFT JOIN Participants p ON s.g_session_id=p.g_session_id AND p.member_id=?
                        WHERE p.g_session_id IS NULL
                        AND (SELECT COUNT(*) FROM Participants p WHERE p.g_session_id = s.g_session_id) < s.capacity
                        """);
            ps.setInt(1, member_id);
            ResultSet rs = ps.executeQuery();
            int counter = 0;
            while (rs.next()){
                ++counter;
                Console.print("\nName: " + rs.getString("name") + " (ID: " + rs.getInt("g_session_id") + ")" +
                        "\nDescription: " + rs.getString("description") +
                        "\nPrice: $" + rs.getFloat("price") +
                        "\nTrainer: " + rs.getString("full_name") + "\n");
            }
            if (counter == 0){
                Console.print("No available times.");
                return;
            }

            int choice = Console.getIntInput("Select ID of course to register for > ");
            ps = con.prepareStatement("INSERT INTO Participants (g_session_id, member_id) VALUES (?, ?)");
            ps.setInt(1, choice);
            ps.setInt(2, member_id);

            try {
                ps.executeUpdate();
                ps = con.prepareStatement("""
                        SELECT s.g_session_id, s.name, s.description, s.price, s.moment, s.capacity,
                        CONCAT(t.first_name, ' ', t.last_name) AS trainer_name, r.number
                        FROM GroupSession s JOIN Trainer t ON s.trainer_id=t.id
                        JOIN Room r ON s.room_id=r.room_id WHERE g_session_id=? LIMIT 1
                        """);
                ps.setInt(1, choice);
                rs = ps.executeQuery();
                rs.next();
                sessions.put(choice, new SessionGroup(choice, rs.getString("name"), rs.getString("description"),
                        rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"), rs.getString("number"), rs.getInt("capacity")));
                Console.print("Successfully registered.");
            }
            catch (SQLException e){
                Console.print(e.getMessage());
                Console.print("No course with ID " + choice);
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Create a new group session and add it to the database and hash table
     */
    @Override
    protected void create()
    {
        int input = 1;
        while (input != 2){
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO GroupSession (name, description, price," +
                        "moment, trainer_id, room_id) VALUES (?,?,?,?,?,?) RETURNING g_session_id");
                String name = Console.getStrInput("Session Name > ", Constants.MAX_OBJECT_NAME_LENGTH);
                String description = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
                BigDecimal price = new BigDecimal(Console.getPrice("Price > "));
                String timestamp = Console.getTimestamp("Date and Time > ");

                ps.setString(1, name);
                ps.setString(2, description);
                ps.setFloat(3, price.floatValue());
                ps.setString(4, timestamp);
                if (userType == Constants.UserType.Trainer){ ps.setInt(5, member_id); }
                else { ps.setInt(5, getTrainerId()); }
                ps.setInt(6, getRoomId());

                ResultSet rs = ps.executeQuery();
                rs.next();
                ps = con.prepareStatement("""
                       SELECT s.g_session_id, s.name, s.description, s.price, s.moment,
                       CONCAT(t.first_name, ' ', t.last_name) AS trainer_name, r.number
                       FROM GroupSession s JOIN Trainer t ON s.trainer_id=t.id
                       JOIN Room r ON s.room_id=r.room_id
                       WHERE g_session_id=? LIMIT 1
                       """);
                ps.setInt(1, rs.getInt(1));
                rs = ps.executeQuery();
                rs.next();
                sessions.put(rs.getInt("g_session_id"), new SessionGroup(rs.getInt("g_session_id"), rs.getString("name"), rs.getString("description"),
                        rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"), rs.getString("number"), rs.getInt("capacity")));
                return;
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }
            input = Console.getIntInput("Try again (1) or back (2) > ", 1, 2);
        }
    }

    /**
     * Helper function for getting the ID of the trainer that will run the session.
     * @return the selected trainer ID
     * @throws SQLException - throws if no trainer with specified ID found
     */
    private int getTrainerId() throws SQLException
    {
        while (true){
            int choice = Console.getIntInput("Enter trainer ID (1) or display all trainer IDs (2) > ", 1, 2);
            if (choice == 1){
                choice = Console.getIntInput("Enter trainer ID > ");
                PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Trainer WHERE id=?");
                ps.setInt(1, choice);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0){
                    return choice;
                }
                else {
                    throw new SQLException("No trainer with that ID found.");
                }
            }
            else {
                PreparedStatement ps = con.prepareStatement("SELECT CONCAT(last_name, ', ', first_name) AS name, id FROM Trainer ORDER BY name ASC");
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    Console.print(rs.getString("name") + " (ID: " + rs.getInt("id") + ")");
                }
            }
        }
    }

    /**
     * Helper function for getting the ID of the rom that the session will take place in.
     * @return the selected room ID
     * @throws SQLException - throws if no room with specified ID found
     */
    private int getRoomId() throws SQLException
    {
        while (true){
            int choice = Console.getIntInput("Enter room ID (1) or display rooms (2) > ", 1, 2);
            if (choice == 1){
                choice = Console.getIntInput("Enter room ID > ");
                PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Room WHERE room_id=?");
                ps.setInt(1, choice);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0){
                    return choice;
                }
                else {
                    throw new SQLException("No room with that ID found.");
                }
            }
            else {
                PreparedStatement ps = con.prepareStatement("SELECT number, description, room_id FROM Room");
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    Console.print(rs.getString("number") + " (" + rs.getInt("room_id") + ") (" + rs.getString("description") + ")");
                }
            }
        }
    }

    /**
     * Prompt the user to enter the group session ID they wish to delete and then
     * delete it from the database and the hash table if it exists.
     */
    @Override
    protected void delete()
    {
        if (sessions.isEmpty()){
            Console.print("Nothing to delete.");
            return;
        }
        Console.print(summary());
        int key = Console.getIntInput("Enter session ID to cancel > ");
        if (sessions.containsKey(key)){
            if (userType == Constants.UserType.Member){ sessions.get(key).removeMember(this.member_id); }
            else { sessions.get(key).deleteFromDB(); }
            sessions.remove(key);
        }
        else {
            Console.print("Session with that ID not found.");
        }
    }

    /**
     * Display all group sessions.
     */
    @Override
    protected void display()
    {
        Console.print(full());
    }

    /**
     * Gives a summarized string representation of the group sessions.
     * @return summarized string representation of the group sessions
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<SessionGroup> enumeration = sessions.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }

    /**
     * Gives a string representation of all the group sessions in the hash
     * table with all the information.
     * @return string representation of the group sessions
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<SessionGroup> enumeration = sessions.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }
}
