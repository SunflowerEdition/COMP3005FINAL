import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Metric {
    private final Connection con;
    private final int id;
    private final Date measuredOn;
    private final String type;
    private final Float value;

    Metric(int id, Date measuredOn, String type, Float value)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.measuredOn = measuredOn;
        this.type = type;
        this.value = value;
    }

    /**
     * Getter for the metric type.
     * @return the metric type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Deletes the metric from the database
     */
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM HealthMetric WHERE health_metric_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            Console.print("Successfully deleted health metric.");
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
        return "Date: " + measuredOn + " (" + type + ":" + value + ") (ID: " + id + ")\n";
    }

    /**
     * Gives a string representation of the class.
     * @return string representation of the class
     */
    @Override
    public String toString()
    {
        return "\nDate: " + measuredOn +
                "\nType: " + type +
                "\nValue: " + value + "\n\n";
    }
}
