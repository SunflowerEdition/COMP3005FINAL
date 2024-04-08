public class Trainer extends User {
    private final MemberViewer memberViewer;
    private final SessionTracker sessionTracker;

    Trainer(int id, String firstName, String lastName)
    {
        super(id, firstName, lastName, Constants.UserType.Trainer);
        this.memberViewer = new MemberViewer();
        this.sessionTracker = new SessionTracker(id, Constants.UserType.Trainer);
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int input = 1;
        while (input != 0){
            input = Console.getIntInput("Logout (0), Schedule (1), View Member Profiles (2), or Update Personal Info (3)  > ", 0, 3);

            if (input == 1){ sessionTracker.runChoice().runChoice(); }
            else if (input == 2){ memberViewer.runChoice(); }
            else if (input == 3) { updatePersonalInfo(); }
        }
    }
}
