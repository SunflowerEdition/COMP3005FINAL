import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class Membership {
    private final Connection con;
    private final int id;
    private final int memberId;
    private Constants.MembershipStatus status;
    private Date startDate;
    private Date endDate;
    private float balance;

    Membership(int id, int memberId, String status, Date startDate, Date endDate, float balance)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.memberId = memberId;
        if (status.equals("A")){ this.status = Constants.MembershipStatus.Active; }
        else if (status.equals("I")){ this.status = Constants.MembershipStatus.Inactive; }
        else { this.status = Constants.MembershipStatus.Frozen; }
        this.startDate = startDate;
        this.endDate = endDate;
        this.balance = balance;
    }

    /**
     * Getter for the membership's member ID.
     * @return ID of the member the membership belongs to
     */
    public int getMemberID()
    {
        return this.memberId;
    }

    public void setStatus(Constants.MembershipStatus stat)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Membership SET status=? WHERE membership_id=?");
            ps.setInt(2, id);
            if (stat == Constants.MembershipStatus.Active){ ps.setString(1, "A"); }
            else if (stat == Constants.MembershipStatus.Inactive){ ps.setString(1, "I"); }
            else { ps.setString(1, "F"); }
            ps.executeUpdate();
            status = stat;
            Console.print("Successfully updated membership");
        }
        catch(SQLException e) {
            Console.print(e.getMessage());
        }
    }

    public void setStartDate(Date date)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Membership SET start_date=? WHERE membership_id=?");
            ps.setDate(1, date);
            ps.setInt(2, id);
            ps.executeUpdate();
            startDate = date;
            Console.print("Successfully updated start date.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    public void setEndDate(Date date)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Membership SET end_date=? WHERE membership_id=?");
            ps.setDate(1, date);
            ps.setInt(2, id);
            ps.executeUpdate();
            endDate = date;
            Console.print("Successfully updated end date.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    public void makePayment(Float payment)
    {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            PreparedStatement ps = con.prepareStatement("UPDATE Membership SET balance=? WHERE membership_id=?");
            ps.setFloat(1, Float.parseFloat(df.format(balance - payment)));
            ps.setInt(2, id);
            ps.executeUpdate();
            balance -= payment;
            Console.print("Payment successful.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes the metric from the database
     */
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Membership WHERE membership_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            Console.print("Membership successfully deleted.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Gives a summarized string representation of the class.
     * @return summarized string representation of the class
     */
    public String summary()
    {
        return "ID: " + id + " (" + startDate + " TO " + endDate + ") (" + status + ") ($" + balance + ")\n";
    }

    /**
     * Gives a string representation of the class.
     * @return string representation of the class
     */
    @Override
    public String toString()
    {
        return "\nMember ID: " + memberId + " (ID: " + id + ")" +
                "\nStatus: " + status.name() +
                "\nStart: " + startDate +
                "\nEnd: " + endDate +
                "\nBalance: " + balance + "\n\n";
    }
}
