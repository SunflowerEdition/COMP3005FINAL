import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberViewer {
    Connection con;

    MemberViewer()
    {
        this.con = Database.getConnection();
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    public void runChoice()
    {
        int choice = Console.getIntInput("Search by first name (1), last name (2), or quick display all members (3) > ");
        if (choice == 1){ search(false); }
        else if (choice == 2){ search(true); }
        else { quickDisplay(); }
    }

    /**
     * Prompt the user to enter a name then display all users with that name
     * @param last - true if searching for last name, false if searching for first name
     */
    private void search(boolean last)
    {
        String result;
        try {
            PreparedStatement ps;
            if (last){
                ps = con.prepareStatement("SELECT * FROM Member WHERE last_name=? ORDER BY last_name ASC");
                result = "Results for members with last name: ";
            }
            else {
                ps = con.prepareStatement("SELECT * FROM Member WHERE first_name=? ORDER BY last_name ASC");
                result = "Results for members with first name: ";
            }

            String name = Console.getStrInput("Name > ", Constants.MAX_NAME_LENGTH);
            result += name + "\n";
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            Console.print(result);
            while (rs.next()){
                String output = "Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + " (" + rs.getInt("id") + ")" +
                        "\nEmail: " + rs.getString("email") +
                        "\nPhone Number: " + rs.getString("phone_number") +
                        "\nAddress: " + rs.getString("address") +
                        "\nGender: " + rs.getString("gender") +
                        "\nBirthday: " + rs.getDate("birthday") + '\n';
                Console.print(output);
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }


    /**
     * Display the names and ID's of all users in the database
     */
    private void quickDisplay()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT CONCAT(last_name, ', ', first_name) AS name, id FROM Member ORDER BY name ASC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Console.print(rs.getString("name") + " (" + rs.getInt("id") + ")");
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }
}
