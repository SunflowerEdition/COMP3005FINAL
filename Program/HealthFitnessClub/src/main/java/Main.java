import java.sql.*;
import java.sql.Date;

public class Main {
    public static Connection con = Database.getConnection();

    /**
     * This is the main method of the program.
     * Servers as the entry point of the application.
     * @param args - the command-line arguments passed to the program.
     */
    public static void main(String[] args){
        while (true){
            User user = null;

            int input = Console.getIntInput("Welcome to Health and Fitness Club. Member Login (1), Trainer Login (2), " +
                    "Admin Login (3), Register (4), or Exit (5) > ", 1, 5);


            if (input < 4){
                if (input == 1){ user = login(Constants.UserType.Member); }
                else if (input == 2){ user = login(Constants.UserType.Trainer); }
                else { user = login(Constants.UserType.Admin); }
                if (user == null){ continue; }
            }
            else if (input == 4) {
                register();
                continue;
            }
            else if (input == 5){
                break;
            }

            if (user == null){
                Console.print("Error: User null after login.");
                continue;
            }

            user.homeDisplay();
            user.runChoice();
        }
    }

    /**
     * Helper method that runs through the login process.
     * @param type - the type of the user trying to log in
     * @return the user object of the newly logged-in user.
     */
    private static User login(Constants.UserType type)
    {
        int input = 0;

        while (input != 2){
            String email = Console.getStrInput("Enter email > ");
            String password = Console.getStrInput("Enter password > ");

            String sql = "SELECT * FROM ";

            if (type == Constants.UserType.Member){ sql += "Member WHERE email=? AND password=?"; }
            else if (type == Constants.UserType.Trainer){ sql += "Trainer WHERE email=? AND password=?"; }
            else { sql += "Admin WHERE email=? AND password=?"; }

            try {
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()){
                    int id = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    if (type == Constants.UserType.Member){ return new Member(id, firstName, lastName); }
                    else if (type == Constants.UserType.Trainer){ return new Trainer(id, firstName, lastName); }
                    else { return new Admin(id, firstName, lastName); }
                }
                else {
                    Console.print("Incorrect email/password combination.");
                }
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }

            input = Console.getIntInput("Try again (1) or Back (2) > ", 1, 2);
        }
        return null;
    }

    /**
     * Helper function that runs through the registration process.
     */
    private static void register()
    {
        int input = 0;
        String confirmSql = "SELECT * FROM Member WHERE email=?";
        String registerSql = "INSERT INTO Member (email, first_name, last_name, phone_number, address, gender, " +
                "birthday, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        while (input != 2){
            try {
                String email = Console.getStrInput("Enter email > ", Constants.MAX_EMAIL_LENGTH);
                String password = Console.getStrInput("Enter password > ", Constants.MAX_PASSWORD_LENGTH);
                String passwordConf = Console.getStrInput("Confirm password > ", Constants.MAX_PASSWORD_LENGTH);

                PreparedStatement ps = con.prepareStatement(confirmSql);
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                if (rs.next()){
                    Console.print("Email already in use.");
                }
                else if (!password.equals(passwordConf)) {
                    Console.print("Passwords do not match.");
                }
                else {
                    ps = con.prepareStatement(registerSql);
                    ps.setString(1, email);
                    ps.setString(8, password);

                    String firstName = Console.getStrInput("Enter first name > ", Constants.MAX_NAME_LENGTH);
                    ps.setString(2, firstName);

                    String lastName = Console.getStrInput("Enter last name > ", Constants.MAX_NAME_LENGTH);
                    ps.setString(3, lastName);

                    String phoneNumber = Console.getStrInput("Enter phone number > ", Constants.MAX_PHONE_NUMBER_LENGTH);
                    ps.setString(4, phoneNumber);

                    String address = Console.getStrInput("Enter address > ", Constants.MAX_ADDRESS_LENGTH);
                    ps.setString(5, address);

                    Date birthday = Console.getDate("Enter birthday (yyyy-mm-dd) > ");
                    ps.setDate(7, birthday);
                    int genderInput = Console.getIntInput("Male (1), Female (2), or Other (3) > ", 1, 3);

                    if (genderInput == 1){ ps.setString(6, "M"); }
                    else if (genderInput == 2) { ps.setString(6, "F"); }
                    else { ps.setString(6, "O"); }

                    ps.executeUpdate();
                    Console.print("Successfully created user.");
                    return;
                }
            }
            catch (SQLException e){
                Console.print(e.getMessage());
            }

            input = Console.getIntInput("Try again (1) or Back (2) > ", 1, 2);
        }
    }
}

