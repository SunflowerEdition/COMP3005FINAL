import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Equipment {
    private final Connection con;
    private final int id;
    private final String description;
    private String lastMaintainTimestamp;

    Equipment(int id, String description, String lastMaintainTimestamp)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.description = description;
        this.lastMaintainTimestamp = lastMaintainTimestamp;
    }

    /**
     * Getter function for the ID of the equipment.
     * @return id of the equipment
     */
    public int getId()
    {
        return id;
    }

    /**
     * Updates the last maintained timestamp in the database
     * @param timestamp - last maintained timestamp
     */
    public void maintained(String timestamp)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Equipment SET last_maintain=? WHERE equipment_id=?");
            ps.setString(1, timestamp);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            this.lastMaintainTimestamp = timestamp;
            Console.print("Successfully updated last maintenance timestamp.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Gives a string representation of the piece of equipment
     * @return string representation of the piece of equipment
     */
    @Override
    public String toString()
    {
        return "\t" + description + " (ID: " + id + ") (Last Maintained: " + lastMaintainTimestamp + ")\n";
    }
}
