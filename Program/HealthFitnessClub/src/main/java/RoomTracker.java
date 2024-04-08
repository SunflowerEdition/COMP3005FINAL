import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class RoomTracker extends Tracker {
    private final Hashtable<Integer, Room> rooms;

    RoomTracker(int adminId){
        super(adminId);
        rooms = new Hashtable<Integer, Room>();
        initialize();
    }

    /**
     * Initializes the values of the hash table using the database information
     */
    @Override
    protected void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Room");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                rooms.put(rs.getInt("room_id"), new Room(rs.getInt("room_id"), rs.getString("number"), rs.getString("description"),
                        rs.getInt("capacity"), rs.getString("last_clean")));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Serves as control flow for deciding what the user chooses to do
     */
    @Override
    public void runChoice()
    {
        int choice = Console.getIntInput("Display rooms (1), create new room (2), " +
                "modify room equipment (3), clean/maintain room (4) > ", 1, 4);
        if (choice == 1){ display(); }
        else if (choice == 2){ create(); }
        else if (choice == 3){ modify(); }
        else { clean(); }
    }

    /**
     * Creates a new room in the database and adds it to the hash table.
     */
    @Override
    protected void create()
    {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Room (number, description, capacity, last_clean) VALUES (?, ?, ?, ?) RETURNING room_id");
            String number = Console.getStrInput("Room number > ", Constants.MAX_ROOM_NUMBER);
            String description = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
            int capacity = Console.getIntInput("Capacity > ");
            String lastClean = Console.getTimestamp("Last Clean > ");

            ps.setString(1, number);
            ps.setString(2, description);
            ps.setInt(3, capacity);
            ps.setString(4, lastClean);

            ResultSet rs = ps.executeQuery();
            rs.next();
            int roomId = rs.getInt(1);

            rooms.put(roomId, new Room(roomId, number, description, capacity, lastClean));
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Prompt the user to enter the ID of the room they with to delete
     * and deletes it along with all Equipment still linked to this room
     * from the database and removes it from the hash table.
     */
    @Override
    protected void delete()
    {
        /*
        Console.print(summary());
        int key = Console.getIntInput("Enter ID of room to delete (and all equipment still linked to this room) > ");
        if (rooms.containsKey(key)){
            rooms.get(key).deleteFromDB();
            rooms.remove(key);
        }
        else {
            Console.print("No room with that ID found.");
        }
        */

        // Commented out because I don't believe Rooms should be able to be deleted for archival purposes.
    }

    /**
     * Displays all rooms along with their equipment.
     */
    @Override
    protected void display()
    {
        Console.print(full());
    }

    /**
     * Gives user the option to add new equipment to a room, delete an equipment,
     * or move a piece of equipment to a different room.
     */
    private void modify()
    {
        Console.print(summary());
        int key = Console.getIntInput("Enter room ID to modify > ");
        if (!rooms.containsKey(key)){
            Console.print("No room with that ID found.");
            return;
        }


        int choice = Console.getIntInput("Add new equipment (1), delete equipment (2), move equipment to another room (3) > ", 1, 3);
        if (choice == 1){
            rooms.get(key).createEquipment();
        }
        else if (choice == 2){
            rooms.get(key).deleteEquipment();
        }
        else {
            Console.print(rooms.get(key).allEquipment());
            int equipId = Console.getIntInput("Enter Equipment ID to move > ");
            if (!rooms.get(key).hasEquipment(equipId)){
                Console.print("No equipment with that ID found in this room.");
                return;
            }

            Console.print(summary());
            int roomId = Console.getIntInput("Enter Room ID to move equipment to > ");
            if (!rooms.containsKey(roomId)){
                Console.print("No room with that ID found.");
                return;
            }

            rooms.get(roomId).addEquipment(rooms.get(key).removeEquipment(equipId));
        }
    }

    /**
     * Update the last time a room was cleaned or maintenance
     * was done to a piece of equipment.
     */
    private void clean()
    {
        Console.print(summary());
        int key = Console.getIntInput("Enter Room ID > ");
        if (!rooms.containsKey(key)){
            Console.print("No room with that ID found.");
            return;
        }

        int choice = Console.getIntInput("Room was cleaned (1) or equipment maintained (2) > ");
        if (choice == 1){
            String timestamp = Console.getTimestamp("Enter when room was cleaned > ");
            rooms.get(key).roomCleaned(timestamp);
        }
        else {
            rooms.get(key).equipmentMaintained();
        }
    }

    /**
     * Gives a summarized string representation of the rooms.
     * @return summarized string representation of the rooms
     */
    @Override
    protected String summary()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Room> enumeration = rooms.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement().summary());
        }
        return sb.toString();
    }

    /**
     * Gives a string representation of all the rooms in the hash
     * table with all the information.
     * @return string representation of the rooms
     */
    @Override
    protected String full()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Room> enumeration = rooms.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }
}
