import java.sql.Connection;

public abstract class Session {
    protected final Connection con;
    protected final int id;
    protected String name;
    protected String description;
    protected float price;
    protected String timestamp;
    protected String trainerName;
    protected String roomNumber;

    Session(int id, String name, String description, float price, String timestamp, String trainerName, String roomNumber)
    {
        this.con = Database.getConnection();
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.timestamp = timestamp;
        this.trainerName = trainerName;
        this.roomNumber = roomNumber;
    }

    public abstract void removeMember(int memberId);
    public abstract void deleteFromDB();
    public abstract String summary();
    public abstract String toString();
}
