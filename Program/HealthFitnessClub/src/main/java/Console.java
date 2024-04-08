import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Scanner;

// Static class for printing messages and getting values
public class Console {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Outputs the string to the console.
     * @param message = the message to print
     */
    public static void print(String message)
    {
        System.out.println(message);
    }

    /**
     * Prompts user to enter string value.
     * @param message - the prompt to show the user
     * @return the user's input
     */
    public static String getStrInput(String message)
    {
        print(message);
        return scanner.nextLine();
    }

    /**
     * Prompts user to enter string value and checks
     * that its length is shorter than specified value.
     * @param message - the prompt to show the user
     * @param max - max length of the user's input
     * @return the user's input
     * @throws SQLException - throws if length is too long
     */
    public static String getStrInput(String message, int max)
            throws SQLException
    {
        String output = getStrInput(message);
        checkInputLength(output, max);
        return output;
    }

    /**
     * Prompts user to enter date and checks
     * that it is in the correct format (YYYY-MM-DD).
     * @param message - the prompt to show the user
     * @return the date the user entered
     */
    public static Date getDate(String message){
        while (true){
            String input = getStrInput(message);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                java.util.Date utilDate = format.parse(input);
                return new Date(utilDate.getTime());
            }
            catch (ParseException e){
                print("Incorrect format.");
            }
        }
    }

    /**
     * Prompts user to enter price and checks
     * that it is in the correct format (XX.XX).
     * @param message - the prompt to show the user
     * @return the price the user entered
     */
    public static String getPrice(String message){
        while (true){
            String input = getStrInput(message);
            if (input.matches("\\d{2}\\.\\d{2}")){
                return input;
            }
            else {
                print("Must be in XX.XX format.");
            }
        }
    }

    /**
     * Prompts the user to enter a timestamps and checks
     * that it is in the correct format (YYYY-MM-DD HH:MM).
     * @param message - the prompt to show the user
     * @return the timestamp the user entered
     */
    public static String getTimestamp(String message){
        while (true){
            String input = getStrInput(message);
            if (input.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")){
                return input;
            }
            else {
                print("Must be in 'YYYY-MM-DD HH:MM' format.");
            }
        }
    }

    /**
     * Prompts the user to enter an integer value.
     * @param message - the prompt to show the user
     * @return the integer the user entered
     */
    public static int getIntInput(String message){
        while (true){
            String input = getStrInput(message);

            try {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e){
                print("Invalid input.");
            }
        }
    }

    /**
     * Prompts the user to enter an integer value and checks
     * that it is within the specified range.
     * @param message - the prompt to show the user
     * @param lower - the lower bound (inclusive)
     * @param upper - the upper bound (inclusive)
     * @return the integer the user entered
     */
    public static int getIntInput(String message, int lower, int upper)
    {
        while (true){
            String input = getStrInput(message);

            try {
                int num = Integer.parseInt(input);
                if (num < lower || num > upper){
                    print("Input out of range.");
                }
                else {
                    return num;
                }
            }
            catch (NumberFormatException e){
                print("Invalid input.");
            }
        }
    }

    /**
     *  Prompts the user to enter a float value.
     * @param message - the prompt ot show the user
     * @return the float the user entered
     */
    public static float getFloatInput(String message)
    {
        while (true){
            String input = getStrInput(message);
            try {
                return Float.parseFloat(input);
            }
            catch (NumberFormatException e){
                print("Invalid input.");
            }
        }
    }

    /**
     * Helper function that checks that the length of the
     * input is not longer than its max value.
     * @param input - the string to verify
     * @param max - the max length of the string
     * @throws SQLException - throws if length too long
     */
    private static void checkInputLength(String input, int max)
            throws SQLException
    {
        if (input.length() > max){
            throw new SQLException("Exceeded max length: " + max);
        }
    }
}
