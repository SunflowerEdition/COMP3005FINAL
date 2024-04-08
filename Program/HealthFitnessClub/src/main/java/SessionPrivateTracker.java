import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class SessionPrivateTracker extends Tracker {
    private final Constants.UserType userType;
    private final Hashtable<Integer, SessionPrivate> sessions;

    SessionPrivateTracker(int member_id, Constants.UserType admin)
    {
        super(member_id);
        this.userType = admin;
        this.sessions = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    @Override
    public void initialize()
    {
        String sql = """
                SELECT
                p.p_session_id, p.name, p.description, p.price, p.moment,
                CONCAT(m.first_name, ' ', m.last_name) AS member_name,
                CONCAT(t.first_name, ' ', t.last_name) AS trainer_name,
                r.number
                FROM PersonalSession p
                LEFT JOIN Member m ON p.member_id = m.id
                JOIN Trainer t ON p.trainer_id = t.id
                JOIN Room r ON p.room_id = r.room_id
                """;

        PreparedStatement ps;
        try {
            if (userType == Constants.UserType.Trainer){
                ps = con.prepareStatement(sql + " WHERE trainer_id=?");
                ps.setInt(1, member_id);
            }
            else if (userType == Constants.UserType.Member) {
                ps = con.prepareStatement(sql + " WHERE member_id=?");
                ps.setInt(1, member_id);
            }
            else {
                ps = con.prepareStatement(sql);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                sessions.put(rs.getInt("p_session_id"), new SessionPrivate(rs.getInt("p_session_id"), rs.getString("name"),
                        rs.getString("description"), rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"),
                        rs.getString("number"), rs.getString("member_name")));
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
            choice = Console.getIntInput("Display my private sessions (1), cancel a private session (2), register for a private session (3) > ", 1, 3);
            if (choice == 3){ choice = 4; }
        }
        else if (userType == Constants.UserType.Trainer){
            choice = Console.getIntInput("Display my private sessions (1), cancel a private session (2), create a private session (3) > ", 1, 3);
        }
        else {
            choice = Console.getIntInput("Display all private sessions (1), cancel a private session (2), create a private session (3) > ", 1, 3);
        }

        if (choice == 1){ display(); }
        else if (choice == 2){ delete(); }
        else if (choice == 4){ register(); }
        else { create(); }
    }

    /**
     * List all private sessions then prompt the user to select the
     * ID of the group session they wish to register for then add
     * them to the session.
     */
    private void register()
    {
        try {
            Console.print("Available Private Sessions:");
            PreparedStatement ps = con.prepareStatement(
                    "SELECT p.p_session_id, p.name, p.description, p.price,\n" +
                    "CONCAT(t.first_name, ' ', t.last_name) AS full_name \n" +
                    "FROM \n" +
                    "PersonalSession p \n" +
                    "JOIN Trainer t ON p.trainer_id = t.id\n" +
                    "WHERE member_id IS NULL");
            ResultSet rs = ps.executeQuery();
            int counter = 0;
            while (rs.next()){
                ++counter;
                Console.print("\nName: " + rs.getString("name") + " (ID: " + rs.getInt("p_session_id") + ")" +
                        "\nDescription: " + rs.getString("description") +
                        "\nPrice: $" + rs.getFloat("price") +
                        "\nTrainer: " + rs.getString("full_name") + "\n");
            }
            if (counter == 0){
                Console.print("No available times.");
                return;
            }
            int choice = Console.getIntInput("Select ID of course to register for > ");
            ps = con.prepareStatement("UPDATE PersonalSession SET member_id=? WHERE p_session_id=? AND member_id IS NULL");
            ps.setInt(1, this.member_id);
            ps.setInt(2, choice);
            int updated = ps.executeUpdate();
            if (updated > 0){
                ps = con.prepareStatement("SELECT p.p_session_id, p.name, p.description, p.price, p.moment,\n" +
                        "CONCAT(t.first_name, ' ', t.last_name) AS trainer_name, r.number, \n" +
                        "CONCAT(m.first_name, ' ', m.last_name) AS member_name\n" +
                        "FROM PersonalSession p JOIN Trainer t ON p.trainer_id=t.id\n" +
                        "JOIN Room r ON p.room_id=r.room_id JOIN Member m ON p.member_id=m.id\n" +
                        "WHERE p_session_id=? LIMIT 1");
                ps.setInt(1, choice);
                rs = ps.executeQuery();
                rs.next();
                sessions.put(choice, new SessionPrivate(rs.getInt("p_session_id"), rs.getString("name"), rs.getString("description"),
                        rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"), rs.getString("number"), rs.getString("member_name")));
                Console.print("Successfully registered.");
            }
            else {
                Console.print("No course with ID " + choice);
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Create a new private session and add it to the database and hash table
     */
    @Override
    protected void create()
    {
        int input = 1;
        while (input != 2){
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO PersonalSession (name, description, price," +
                        "moment, trainer_id, room_id) VALUES (?,?,?,?,?,?) RETURNING p_session_id");
                String name = Console.getStrInput("Session Name > ", Constants.MAX_OBJECT_NAME_LENGTH);
                String description = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
                BigDecimal price = new BigDecimal(Console.getPrice("Price > "));
                String timestamp = Console.getTimestamp("Date and Time > ");

                ps.setString(1, name);
                ps.setString(2, description);
                ps.setFloat(3, price.floatValue());
                ps.setString(4, timestamp);
                if (userType == Constants.UserType.Admin){ ps.setInt(5, getTrainerId()); }
                else { ps.setInt(5, member_id); }
                ps.setInt(6, getRoomId());

                ResultSet rs = ps.executeQuery();
                rs.next();
                ps = con.prepareStatement("""
                       SELECT p.p_session_id, p.name, p.description, p.price, p.moment,
                       CONCAT(t.first_name, ' ', t.last_name) AS trainer_name, r.number
                       FROM PersonalSession p JOIN Trainer t ON p.trainer_id=t.id
                       JOIN Room r ON p.room_id=r.room_id
                       WHERE p_session_id=? LIMIT 1
                       """);
                ps.setInt(1, rs.getInt(1));
                rs = ps.executeQuery();
                rs.next();
                sessions.put(rs.getInt("p_session_id"), new SessionPrivate(rs.getInt("p_session_id"), rs.getString("name"), rs.getString("description"),
                        rs.getFloat("price"), rs.getString("moment"), rs.getString("trainer_name"), rs.getString("number"), ""));

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
     * Prompt the user to enter the private session ID they wish to delete and then
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
     * Display all private sessions in the hash table.
     */
    @Override
    protected void display()
    {
        Console.print(full());
    }

    /**
     * Gives a string representation of all the private sessions in the hash
     * table with all the information.
     * @return string representation of the private sessions
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<SessionPrivate> enumeration = sessions.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a summarized string representation of the private sessions.
     * @return summarized string representation of the private sessions
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<SessionPrivate> enumeration = sessions.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }
}
