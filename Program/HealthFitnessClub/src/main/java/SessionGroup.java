import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class SessionGroup extends Session {
    private final Hashtable<Integer, String> members;
    private int capacity;
    SessionGroup(int id, String name, String description, float price, String timestamp, String trainerName, String roomNumber, int capacity)
    {
        super(id, name, description, price, timestamp, trainerName, roomNumber);
        this.members = new Hashtable<>();
        this.capacity = capacity;
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    private void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT p.member_id, CONCAT(m.first_name, ' ', " +
                    "m.last_name) AS full_name FROM Participants p JOIN Member m ON p.member_id=m.id\n" +
                    "WHERE g_session_id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                members.put(rs.getInt("member_id"), rs.getString("full_name"));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Removes the member from the participation list in the database.
     * @param memberId - ID of the member to remove
     */
    @Override
    public void removeMember(int memberId)
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Participants WHERE g_session_id=? AND member_id=?");
            ps.setInt(1, id);
            ps.setInt(2, memberId);
            ps.executeUpdate();
            if (members.remove(memberId) == null){
                Console.print("No member with id " + memberId + " registered.");
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes the group session from the database.
     */
    @Override
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM GroupSession WHERE g_session_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Gives a summarized string representation of the group sessions.
     * @return summarized string representation of the group sessions
     */
    @Override
    public String summary()
    {
        return name + " (" + timestamp + ") (" + roomNumber + ") (ID: " + id + ")\n";
    }

    /**
     * Gives a string representation of the class.
     * @return string representation of the class
     */
    @Override
    public String toString()
    {
        return "Trainer Name: " + trainerName +
                "\nDate: "  + timestamp +
                "\nRoom: " + roomNumber +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nPrice: $" + price + "\n\n";
    }
}
