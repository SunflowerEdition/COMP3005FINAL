import java.sql.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

public class MetricTracker extends Tracker {
    private final Hashtable<Integer, Metric> healthMetrics;
    private final HashSet<String> existingMetrics;

    MetricTracker(int member_id)
    {
        super(member_id);
        this.healthMetrics = new Hashtable<>();
        this.existingMetrics = new HashSet<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    @Override
    protected void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM HealthMetric WHERE member_id=? ORDER BY measured_on ASC");
            ps.setInt(1, member_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                existingMetrics.add(rs.getString("type"));
                healthMetrics.put(rs.getInt("health_metric_id"), new Metric(rs.getInt("health_metric_id"),
                        rs.getDate("measured_on"), rs.getString("type"), rs.getFloat("value")));
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
        int choice = Console.getIntInput("Display stats (1), register metric (2), delete metric (3) > ", 1, 3);
        if (choice == 1){ display(); }
        else if (choice == 2){ create(); }
        else { delete(); }
    }

    /**
     * Creates a new metric in the database and adds it to the hash table.
     */
    @Override
    protected void create()
    {
        int input = 0;
        while (input != 2){
            try {
                Console.print("Existing Types\n" + allTypes());
                PreparedStatement ps = con.prepareStatement("INSERT INTO HealthMetric (measured_on, type, value, member_id) VALUES (?, ?, ?, ?) RETURNING health_metric_id");
                Date measuredOn = Console.getDate("Date (YYYY-MM-DD) > ");
                String type = Console.getStrInput("Metric Type > ", Constants.MAX_TYPE_LENGTH);
                Float value = Console.getFloatInput("Value > ");
                ps.setDate(1, measuredOn);
                ps.setString(2, type);
                ps.setFloat(3, value);
                ps.setInt(4, member_id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int createdId = rs.getInt(1);
                healthMetrics.put(createdId, new Metric(createdId, measuredOn, type, value));
                existingMetrics.add(type);
                Console.print("Successfully created health metric.");
                return;
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }
            input = Console.getIntInput("Try again (1) or back (2) > ", 1, 2);
        }
    }

    /**
     * Prompt the user to enter the ID of the metric they with to delete
     * and deletes it from the database and removes it from the hash table.
     */
    @Override
    protected void delete()
    {
        if (healthMetrics.isEmpty()){
            Console.print("Nothing to delete.");
            return;
        }
        Console.print(summary());
        int key = Console.getIntInput("Enter ID of routine to delete > ");
        if (healthMetrics.containsKey(key)){
            healthMetrics.get(key).deleteFromDB();
            healthMetrics.remove(key);
        }
        else {
            Console.print("Routine with that ID not found.");
        }
    }

    /**
     * Prompts the user to select which metrics they wish to display
     * and then displays them.
     */
    @Override
    protected void display()
    {
        int input = Console.getIntInput("Display by date (1) or by type (2) > ", 1, 2);
        if (input == 1){
            Console.print(full());
        }
        else {
            Console.print(allTypes());
            String type = Console.getStrInput("Select Type > ");
            if (existingMetrics.contains(type)){
                Console.print(full(type));
                Console.print(stats(type));
            }
            else { Console.print("Metrics with that type not found."); }
        }
    }

    /**
     * Returns a string with the overall stats of a metric type.
     * @param type - the metric type
     * @return overall stats
     */
    private String stats(String type)
    {
        try {
            PreparedStatement ps = con.prepareStatement("""
                    SELECT
                    	AVG(value) AS average,
                    	MIN(value) AS minimum,
                    	MAX(value) AS maximum
                    FROM HealthMetric
                    WHERE member_id=? AND type=?""");
            ps.setInt(1, this.member_id);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return "\nMinimum: " + rs.getFloat("minimum") +
                    "\nMaximum: " + rs.getFloat("maximum") +
                    "\nAverage: " + rs.getFloat("average") + '\n';
        }
        catch (SQLException e){
            Console.print(e.getMessage());
            return "";
        }

    }

    /**
     * Gives a string representation of all the metrics in the hash
     * table with all the information.
     * @return string representation of the metrics
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Metric> enumeration = healthMetrics.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a string representation of all the metrics in the hash
     * table of the specified type with all the information.
     * @param type - the type of metrics to display
     * @return string representation of the metrics
     */
    protected String full(String type)
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Metric> enumeration = healthMetrics.elements();
        while (enumeration.hasMoreElements()){
            Metric metric = enumeration.nextElement();
            if (metric.getType().equals(type)){
                sb.append(metric);
            }
        }
        return sb.toString();
    }

    /**
     * Gives a summarized string representation of the metrics.
     * @return summarized string representation of the metrics
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Metric> enumeration = healthMetrics.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }

    /**
     * Gives a string of all metric types present in the database
     * @return string representation of all metric types
     */
    protected String allTypes()
    {
        StringBuilder sb = new StringBuilder();
        for (String type : existingMetrics){
            sb.append(type).append("\n");
        }
        return sb.toString();
    }
}
