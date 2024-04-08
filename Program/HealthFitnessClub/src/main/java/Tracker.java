import java.sql.Connection;

public abstract class Tracker {
    protected final Connection con;
    protected final int member_id;

    Tracker(int member_id)
    {
        this.con = Database.getConnection();
        this.member_id = member_id;
    }

    protected abstract void initialize();
    public abstract void runChoice();
    protected abstract void create();
    protected abstract void delete();
    protected abstract void display();
    protected abstract String summary();
    protected abstract String full();
}
