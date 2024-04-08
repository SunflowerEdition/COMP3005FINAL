import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FitnessGoal {
    private final Connection con;
    private final int id;
    private final String name;
    private final String current;
    private final String target;
    private final Date deadline;
    private Constants.GoalStatus status;

    FitnessGoal(int id, String name, String current, String target, Date deadline, String status)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.name = name;
        this.current = current;
        this.target = target;
        this.deadline = deadline;
        if (status.equals("A")){ this.status = Constants.GoalStatus.Active; }
        else if (status.equals("C")){ this.status = Constants.GoalStatus.Completed; }
        else { this.status = Constants.GoalStatus.Inactive; }
    }

    /**
     * Checks if the status of the fitness goal
     * is the same as the specified status.
     * @param s - the status to verify
     * @return true if the same, false otherwise
     */
    public boolean isStatus(Constants.GoalStatus s)
    {
        return status == s;
    }

    /**
     * Updates the status of the fitness goal in the database.
     * @param s - the new status of the fitness goal
     */
    public void updateToDB(Constants.GoalStatus s)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE FitnessGoal SET status=? WHERE fitness_goal_id=?");
            if (s == Constants.GoalStatus.Active){ ps.setString(1, "A"); }
            else if (s == Constants.GoalStatus.Completed){ ps.setString(1, "C"); }
            else { ps.setString(1, "I"); }
            ps.setInt(2, id);
            ps.executeUpdate();
            this.status = s;
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes the fitness goal from the database
     */
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM FitnessGoal WHERE fitness_goal_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            Console.print("Successfully deleted.");
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
        return "Name: " + name +
                "\nCurrent: " + current +
                "\nTarget: " + target +
                "\nDeadline: " + deadline +
                "\nStatus: " + status.name() + "\n\n";
    }
}
