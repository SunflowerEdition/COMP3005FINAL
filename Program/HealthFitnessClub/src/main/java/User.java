import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class User {
    protected static final Connection con = Database.getConnection();
    protected final int id;
    private final String firstName;
    private final String lastName;
    private final Constants.UserType type;

    User(int id, String firstName, String lastName, Constants.UserType type)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public String homeDisplay()
    {
        return "Welcome " + firstName + " " + lastName + "\n" + "(" + type.name() + ")";
    }

    public abstract void runChoice();

    /**
     * Walks user through steps to change their personal information.
     */
    protected void updatePersonalInfo()
    {
        int choice = Console.getIntInput("Back (0), Change email (1), password (2), address (3), phone number (4), birthday (5), " +
                "gender (6)", 0, 6);

        String tableSql = "";
        if (type == Constants.UserType.Member){ tableSql = "UPDATE Member "; }
        else if (type == Constants.UserType.Trainer){ tableSql = "UPDATE Trainer "; }
        else { tableSql = "UPDATE Admin "; }

        if (choice == 1){ changeEmail(tableSql); }
        else if (choice == 2){ changePassword(tableSql); }
        else if (choice == 3){ changeAddress(tableSql); }
        else if (choice == 4){ changePhoneNumber(tableSql); }
        else if (choice == 5){ changeBirthday(tableSql); }
        else if (choice == 6){ changeGender(tableSql); }
    }

    /**
     * Walks user through steps to change their email.
     */
    private void changeEmail(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET email=? WHERE id=?");
            ps.setString(1, Console.getStrInput("New email > ", Constants.MAX_EMAIL_LENGTH));
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated email.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Walks user through steps to change their password.
     */
    private void changePassword(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET password=? WHERE id=?");
            String password = Console.getStrInput("New password > ", Constants.MAX_PASSWORD_LENGTH);
            String confPass = Console.getStrInput("Confirm password > ", Constants.MAX_PASSWORD_LENGTH);
            if (!password.equals(confPass)){ throw new SQLException("Passwords do not match."); }
            ps.setString(1, password);
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated password.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Walks user through steps to change their address.
     */
    private void changeAddress(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET address=? WHERE id=?");
            ps.setString(1, Console.getStrInput("New address > ", Constants.MAX_ADDRESS_LENGTH));
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated address.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Walks user through steps to change their phone number.
     */
    private void changePhoneNumber(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET phone_number=? WHERE id=?");
            ps.setString(1, Console.getStrInput("New phone number > ", Constants.MAX_PHONE_NUMBER_LENGTH));
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated phone number.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Walks user through steps to change their birthday.
     */
    private void changeBirthday(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET birthday=? WHERE id=?");
            ps.setDate(1, Console.getDate("New birthday > "));
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated birthday.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Walks user through steps to change their gender.
     */
    private void changeGender(String sqlStart)
    {
        try {
            PreparedStatement ps = con.prepareStatement(sqlStart + "SET gender=? WHERE id=?");
            int choice = Console.getIntInput("Male (1), female (2), or other (3) > ", 1, 3);
            if (choice == 1){ ps.setString(1, "M");}
            else if (choice == 2){ ps.setString(1, "F");}
            else { ps.setString(1, "O");}
            ps.setInt(2, id);
            ps.executeUpdate();
            Console.print("Successfully updated gender.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }
}
