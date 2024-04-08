import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class MembershipTracker extends Tracker {
    private final Hashtable<Integer, Membership> memberships;

    MembershipTracker(int adminId)
    {
        super(adminId);
        memberships = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash tables using the database information
     */
    @Override
    protected void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Membership");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                memberships.put(rs.getInt("membership_id"), new Membership(rs.getInt("membership_id"), rs.getInt("member_id"),
                        rs.getString("status"), rs.getDate("start_date"), rs.getDate("end_date"), rs.getFloat("balance")));
            }
        }
        catch(SQLException e) {
            Console.print(e.getMessage());
        }
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int choice = Console.getIntInput("Display memberships (1), create new membership (2), delete membership (3), " +
                "modify existing membership/payment (4) > ", 1, 4);
        if (choice == 1){ display(); }
        else if (choice == 2){ create(); }
        else if (choice == 3){ delete(); }
        else { modify(); }
    }

    /**
     * Prompts user to select membership to update and gives options
     * to change its values
     */
    private void modify()
    {
        int memId = Console.getIntInput("Member ID > ");

        try {
            Console.print(summary(memId));
        }
        catch (Exception e){
            Console.print(e.getMessage());
        }

        int key = Console.getIntInput("Membership ID to update > ");
        if (!memberships.containsKey(key) || memberships.get(key).getMemberID() != memId){
            Console.print("No memberships with that ID found");
            return;
        }

        int choice = Console.getIntInput("Change status (1), start date (2), end date (3), or make payment (4) > ", 1, 4);
        if (choice == 1){
            int status = Console.getIntInput("Active (1), Inactive (2), or Frozen (3) > ");
            if (status == 1){ memberships.get(key).setStatus(Constants.MembershipStatus.Active); }
            else if (status == 2){ memberships.get(key).setStatus(Constants.MembershipStatus.Inactive); }
            else { memberships.get(key).setStatus(Constants.MembershipStatus.Frozen); }
        }
        else if (choice == 2){
            memberships.get(key).setStartDate(Console.getDate("Enter start date > "));
        }
        else if (choice == 3){
            if (Console.getIntInput("Enter end date (1) or remove end date (2) > ", 1, 2) == 2){ memberships.get(key).setEndDate(null); }
            else { memberships.get(key).setEndDate(Console.getDate("Enter end date > ")); }
        }
        else {
            memberships.get(key).makePayment(Float.parseFloat(Console.getPrice("Amount paid > ")));
        }
    }

    /**
     * Create a new membership and add it to the database and hash table
     */
    @Override
    protected void create()
    {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Membership (status, member_id, start_date, end_date, balance) " +
                    "VALUES ('A',?,?,?,?) RETURNING membership_id");
            int memId = Console.getIntInput("Member ID > ");
            Date sDate = Console.getDate("Start Date > ");
            Date eDate = Console.getDate("End Date > ");
            BigDecimal bal = new BigDecimal(Console.getPrice("Balance > "));

            ps.setInt(1, memId);
            ps.setDate(2, sDate);
            ps.setDate(3, eDate);
            ps.setFloat(4, bal.floatValue());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int mId = rs.getInt(1);
            memberships.put(mId, new Membership(mId, memId, "A", sDate, eDate, bal.floatValue()));
            Console.print("Successfully created membership.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Prompt the user to enter the ID of the membership they with to delete
     * and deletes it from the database and removes it from the hash table.
     */
    @Override
    protected void delete()
    {
        int memId = Console.getIntInput("Enter member ID > ");

        try {
            Console.print(summary(memId));
        }
        catch (Exception e){
            Console.print(e.getMessage());
            return;
        }

        int key = Console.getIntInput("Membership ID to delete > ");
        if (memberships.containsKey(key) && memberships.get(key).getMemberID() == memId){
            memberships.get(key).deleteFromDB();
            memberships.remove(key);
        }
        else {
            Console.print("No memberships with that ID found.");
        }
    }

    /**
     * Prompts the user to select the member they wish to view the
     * memberships of then displays them.
     */
    @Override
    protected void display()
    {
        Console.print(full(Console.getIntInput("Enter member ID > ")));
    }

    /**
     * Gives a summarized string representation of the memberships.
     * @return summarized string representation of the memberships
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Membership> enumeration = memberships.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }

    /**
     * Gives a summarize string representation of the memberships of
     * the specified member.
     * @param memberId - ID of the member
     * @return the string presentation of the memberships
     * @throws Exception - throws if no memberships found for specified ID
     */
    private String summary(int memberId) throws Exception
    {
        int counter = 0;
        StringBuilder sb = new StringBuilder();
        Enumeration<Membership> enumeration = memberships.elements();
        while (enumeration.hasMoreElements()){
            Membership membership = enumeration.nextElement();
            if (membership.getMemberID() == memberId){
                ++counter;
                sb.append(membership.summary());
            }
        }

        if (counter == 0){ throw new Exception("No memberships found for that member ID."); }
        return sb.toString();
    }

    /**
     * Gives a string representation of all the memberships in the hash
     * table with all the information.
     * @return string representation of the memberships
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Membership> enumeration = memberships.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a string representation of all memberships related to a member.
     * @param memberId - the ID of the member
     * @return string representation of all memberships
     */
    private String full(int memberId)
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Membership> enumeration = memberships.elements();
        while (enumeration.hasMoreElements()){
            Membership membership = enumeration.nextElement();
            if (membership.getMemberID() == memberId){
                sb.append(membership);
            }
        }
        return sb.toString();
    }

}
