public class SessionTracker {
    private final SessionPrivateTracker privateTracker;
    private final SessionGroupTracker groupTracker;

    SessionTracker(int userId, Constants.UserType userType)
    {
        this.privateTracker = new SessionPrivateTracker(userId, userType);
        this.groupTracker = new SessionGroupTracker(userId, userType);
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    public Tracker runChoice()
    {
        int choice = Console.getIntInput("Private sessions (1), or group sessions (2) > ", 1, 2);
        if (choice == 1){ return privateTracker; }
        else { return groupTracker; }
    }
}
