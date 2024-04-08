public class Constants {
    // Max lengths for various string types
    public static final int MAX_OBJECT_NAME_LENGTH = 40;
    public static final int MAX_TARGET_LENGTH = 20;
    public static final int MAX_NAME_LENGTH = 35;
    public static final int MAX_EMAIL_LENGTH = 50;
    public static final int MAX_PHONE_NUMBER_LENGTH = 15;
    public static final int MAX_ADDRESS_LENGTH = 100;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 100;
    public static final int MAX_FREQUENCY_LENGTH = 15;
    public static final int MAX_TYPE_LENGTH = 30;
    public static final int MAX_ROOM_NUMBER = 4;

    enum UserType {
        Member, Trainer, Admin
    }

    public enum GoalStatus {
        Active, Inactive, Completed
    }

    public enum MembershipStatus {
        Active, Inactive, Frozen
    }
}
