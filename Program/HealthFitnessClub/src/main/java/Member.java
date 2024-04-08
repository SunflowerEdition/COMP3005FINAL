import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Member extends User {
    private final RoutineTracker routineTracker;
    private final FitnessGoalTracker fitnessGoalTracker;
    private final MetricTracker metricTracker;
    private final SessionTracker sessionTracker;

    Member(int id, String firstName, String lastName)
    {
        super(id, firstName, lastName, Constants.UserType.Member);
        this.routineTracker = new RoutineTracker(id);
        this.fitnessGoalTracker = new FitnessGoalTracker(id);
        this.metricTracker = new MetricTracker(id);
        this.sessionTracker = new SessionTracker(id, Constants.UserType.Member);
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int input = 1;
        while (input != 0){
            input = Console.getIntInput("Logout (0), Routines (1), Achievements (2), Health Metrics (3), " +
                    "Schedule (4), or Update Personal Info (5) > ", 0, 5);
            Tracker tracker;

            if (input == 1){ tracker = routineTracker; }
            else if (input == 2){ tracker = fitnessGoalTracker; }
            else if (input == 3){ tracker = metricTracker; }
            else if (input == 4){ tracker = sessionTracker.runChoice(); }
            else if (input == 5){ updatePersonalInfo(); continue; }
            else { continue; }

            tracker.runChoice();
        }
    }
}
