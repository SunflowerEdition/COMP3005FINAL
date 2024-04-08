import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class FitnessGoalTracker extends Tracker {
    private final Hashtable<Integer, FitnessGoal> fitnessGoals;

    FitnessGoalTracker(int member_id)
    {
        super(member_id);
        fitnessGoals = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information.
     */
    @Override
    protected void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM FitnessGoal WHERE member_id=?");
            ps.setInt(1, member_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                fitnessGoals.put(rs.getInt("fitness_goal_id"), new FitnessGoal(rs.getInt("fitness_goal_id"),
                        rs.getString("name"), rs.getString("current"), rs.getString("target"), rs.getDate("deadline"),
                        rs.getString("status")));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }

    }

    /**
     * Serves as control flow for deciding what the user chooses to do.
     */
    @Override
    public void runChoice()
    {
        int choice = Console.getIntInput("Display achievements (1), create achievement (2), delete achievement (3), update status (4) > ", 1, 4);
        if (choice == 1){ display(); }
        else if (choice == 2){ create(); }
        else if (choice == 3){ delete(); }
        else { update(); }
    }

    /**
     * Creates a new fitness goal in the database and adds it to the hash table.
     */
    @Override
    protected void create()
    {
        int input = 0;
        while (input != 2){
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO FitnessGoal (name, current, target, deadline, status, member_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING fitness_goal_id");
                String name = Console.getStrInput("Name > ", Constants.MAX_OBJECT_NAME_LENGTH);
                String current = Console.getStrInput("Current Value > ", Constants.MAX_TARGET_LENGTH);
                String target = Console.getStrInput("Target Goal > ", Constants.MAX_TARGET_LENGTH);
                java.sql.Date date = Console.getDate("Deadline (YYYY-MM-DD) > ");
                ps.setString(1, name);
                ps.setString(2, current);
                ps.setString(3, target);
                ps.setDate(4, date);
                ps.setString(5, "A");
                ps.setInt(6, member_id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int createdId = rs.getInt(1);
                fitnessGoals.put(createdId, new FitnessGoal(createdId, name, current, target, date, "A"));
                Console.print("Successfully created.");
                return;
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }
            input = Console.getIntInput("Try again (1) or back (2) > ", 1, 2);
        }
    }

    /**
     * Prompt the user to enter the ID of the fitness goal they with to delete
     * and delete it from the database and removes it from the hash table.
     */
    @Override
    protected void delete()
    {
        if (fitnessGoals.isEmpty()){
            Console.print("Nothing to delete.");
            return;
        }
        Console.print(summary());
        int key = Console.getIntInput("Enter ID of fitness goal to delete > ");
        if (fitnessGoals.containsKey(key)){
            fitnessGoals.get(key).deleteFromDB();
            fitnessGoals.remove(key);
        }
        else {
            Console.print("Fitness goal with that ID not found.");
        }
    }

    /**
     * Prompts the user to select which fitness goals they wish to display
     * and then displays them.
     */
    @Override
    protected void display()
    {
        int input = Console.getIntInput("Active (1), Inactive (2), Completed (3), or All (4) Achievements > ", 1, 4);
        if (input == 1){ Console.print(full(Constants.GoalStatus.Active)); }
        else if (input == 2){ Console.print(full(Constants.GoalStatus.Inactive)); }
        else if (input == 3) { Console.print(full(Constants.GoalStatus.Completed)); }
        else { Console.print(full()); }
    }

    /**
     * Prompts the user to input the ID of the fitness goal they wish to update
     * and then input the new status of the fitness goal.
     */
    protected void update()
    {
        if (fitnessGoals.isEmpty()){
            Console.print("No fitness goals to update.");
            return;
        }
        Console.print(summary());
        int input = Console.getIntInput("Enter ID of element to update > ");
        int stat = Console.getIntInput("Set Active (1), Inactive (2), or Completed (3) > ", 1, 3);

        if (fitnessGoals.containsKey(input)){
            if (stat == 1){ fitnessGoals.get(input).updateToDB(Constants.GoalStatus.Active); }
            else if (stat == 3){ fitnessGoals.get(input).updateToDB(Constants.GoalStatus.Completed); }
            else { fitnessGoals.get(input).updateToDB(Constants.GoalStatus.Inactive); }
            Console.print("Fitness goal successfully updated.");
        }
        else {
            Console.print("No fitness goal with that id found.");
        }
    }

    /**
     * Gives a string representation of all the fitness goals in the hash
     * table with all the information.
     * @return string representation of the fitness goals
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<FitnessGoal> enumeration = fitnessGoals.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a string representation of all the fitness goals in the hash
     * table with the specified status with all the information.
     * @param status - the status of the fitness goals to display
     * @return string representation of the fitness goals
     */
    protected String full(Constants.GoalStatus status)
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<FitnessGoal> enumeration = fitnessGoals.elements();
        while (enumeration.hasMoreElements()){
            FitnessGoal fg = enumeration.nextElement();
            if (fg.isStatus(status)){
                sb.append(fg);
            }
        }
        return sb.toString();
    }

    /**
     * Gives a summarized string representation of the class.
     * @return summarized string representation of the class
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<FitnessGoal> enumeration = fitnessGoals.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }
}
