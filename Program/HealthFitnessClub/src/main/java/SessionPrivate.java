import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SessionPrivate extends Session {
    private final String memberName;

    SessionPrivate(int id, String name, String description, float price, String timestamp, String trainerName, String roomNumber, String memberName)
    {
        super(id, name, description, price, timestamp, trainerName, roomNumber);
        this.memberName = memberName;
    }

    /**
     * Removes the member from the private session in the database.
     * @param memberId - ID of the member to remove
     */
    @Override
    public void removeMember(int memberId)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE PersonalSession SET member_id=NULL WHERE p_session_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes the private session from the database.
     */
    @Override
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM PersonalSession WHERE p_session_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Gives a summarized string representation of the private sessions.
     * @return summarized string representation of the private sessions
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
        return "Member Name: " + memberName +
                "\nTrainer Name: " + trainerName +
                "\nDate: "  + timestamp +
                "\nRoom: " + roomNumber +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nPrice: $" + price + "\n\n";
    }
}
