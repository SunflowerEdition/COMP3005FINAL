public class Admin extends User {
    private final MemberViewer memberViewer;
    private final SessionTracker sessionTracker;
    private final MembershipTracker membershipTracker;
    private final RoomTracker roomTracker;

    Admin(int id, String firstName, String lastName)
    {
        super(id, firstName, lastName, Constants.UserType.Admin);
        memberViewer = new MemberViewer();
        sessionTracker = new SessionTracker(id, Constants.UserType.Admin);
        membershipTracker = new MembershipTracker(id);
        roomTracker = new RoomTracker(id);
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int input = 1;
        while (input != 0){
            input = Console.getIntInput("Logout (0), Display Members (1), Rooms/Equipment (2), Classes (3), Billing (4), or Update Personal Info (5) > ", 0, 5);
            Tracker tracker;

            if (input == 1){ memberViewer.runChoice(); continue; }
            else if (input == 2){ tracker = roomTracker; }
            else if (input == 3){ tracker = sessionTracker.runChoice(); }
            else if (input == 4){ tracker = membershipTracker; }
            else if (input == 5){ updatePersonalInfo(); continue; }
            else { continue; }

            tracker.runChoice();
        }
    }
}
