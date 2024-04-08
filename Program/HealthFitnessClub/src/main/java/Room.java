import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class Room {
    private final Connection con;
    private final int id;
    private final String number;
    private final String description;
    private final int capacity;
    private String lastCleanTimestamp;
    private final Hashtable<Integer, Equipment> equipment;

    Room(int id, String number, String description, int capacity, String lastCleanTimestamp){
        this.con = Database.getConnection();
        this.id = id;
        this.number = number;
        this.description = description;
        this.capacity = capacity;
        this.lastCleanTimestamp = lastCleanTimestamp;
        this.equipment = new Hashtable<>();
        initialize();
    }

    /**
     * Initializes the values of the hash table using the database information
     */
    private void initialize()
    {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Equipment WHERE room_id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                equipment.put(rs.getInt("equipment_id"), new Equipment(rs.getInt("equipment_id"),
                        rs.getString("description"), rs.getString("last_maintain")));
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Checks if the equipment hash table contains the specified ID.
     * @param equipId - the equipment ID to check for
     * @return true if it is present, false otherwise
     */
    public boolean hasEquipment(int equipId)
    {
        return equipment.containsKey(equipId);
    }

    /**
     * Updates the last time the room was cleaned in the database.
     * @param timestamp - timestamp of most recent room clean
     */
    public void roomCleaned(String timestamp)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Room SET last_clean=? WHERE room_id=?");
            ps.setString(1, timestamp);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            this.lastCleanTimestamp = timestamp;
            Console.print("Successfully updated last clean.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Updates the last time a piece of equipment was maintained.
     */
    public void equipmentMaintained()
    {
        Console.print(allEquipment());
        int key = Console.getIntInput("Select maintained equipment ID > ");
        if (!equipment.containsKey(key)){
            Console.print("No equipment with that ID found.");
            return;
        }
        String timestamp = Console.getTimestamp("Enter when equipment was maintained > ");
        equipment.get(key).maintained(timestamp);
    }

    /**
     * Deletes the room and all equipment in the room from the database.
     */
    public void deleteFromDB()
    {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Room WHERE room_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Removes the specified equipment from the hash table
     * without deleting it from the database.
     * @param equipId - the ID of the equipment to remove
     * @return the equipment removed
     */
    public Equipment removeEquipment(int equipId)
    {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Equipment SET room_id=NULL WHERE equipment_id=?");
            ps.setInt(1, equipId);
            int rowsChanged = ps.executeUpdate();
            if (rowsChanged == 0){
                Console.print("No equipment with that ID found.");
                return null;
            }
            else {
                Console.print("Successfully removed equipment from room.");
                return equipment.remove(equipId);
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
            return null;
        }
    }

    /**
     * Adds the specified equipment to the hash table without
     * creating a new instance in the database
     * @param equip - the equipment to add to the hash table
     */
    public void addEquipment(Equipment equip)
    {
        if (equip == null){ return; }

        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Equipment SET room_id=? WHERE equipment_id=?");
            ps.setInt(1, this.id);
            ps.setInt(2, equip.getId());
            ps.executeUpdate();
            equipment.put(equip.getId(), equip);
            Console.print("Successfully added equipment to room.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Creates a new instance of equipment and adds it to the
     * database and the hash table.
     */
    public void createEquipment()
    {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Equipment (description, last_maintain, room_id) VALUES (?,?,?) RETURNING equipment_id");
            String description = Console.getStrInput("Description > ", Constants.MAX_DESCRIPTION_LENGTH);
            String lastMaintain = Console.getTimestamp("Last maintained > ");
            ps.setString(1, description);
            ps.setString(2, lastMaintain);
            ps.setInt(3, this.id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int equipId = rs.getInt(1);
            equipment.put(equipId, new Equipment(equipId, description, lastMaintain));
            Console.print("Successfully created new equipment.");
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Deletes a piece of equipment with the specified ID from the
     * hash table as well as the database.
     */
    public void deleteEquipment()
    {
        try {
            if (equipment.isEmpty()){
                Console.print("No equipment in this room to delete.");
                return;
            }
            Console.print(allEquipment());
            PreparedStatement ps = con.prepareStatement("DELETE FROM Equipment WHERE equipment_id=? AND room_id=?");
            int equipId = Console.getIntInput("ID of room to delete > ");
            ps.setInt(1, equipId);
            ps.setInt(2, this.id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0){
                Console.print("No equipment with that ID found.");
            }
            else {
                equipment.remove(equipId);
                Console.print("Successfully Deleted.");
            }
        }
        catch (SQLException e){
            Console.print(e.getMessage());
        }
    }

    /**
     * Gives a string representation of all the equipment
     * present in the room.
     * @return string representation of the equipment
     */
    public String allEquipment()
    {
        StringBuilder sb = new StringBuilder();
        Enumeration<Equipment> enumeration = equipment.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }

    /**
     * Gives a summarized string representation of the room.
     * @return summarized string representation of the room
     */
    public String summary()
    {
        return "Number: " + number + " (ID: " + id + ") (" + description + ")\n";
    }

    /**
     * Gives a string representation of the room along with
     * all the equipment present in the room.
     * @return string representation of the room
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(
                "\nNumber: " + number + " (ID: " + id + ")\nDescription: " + description
                + "\nCapacity: " + capacity + "\nLast Clean: " + lastCleanTimestamp
                + "\nEquipment:\n"
        );
        Enumeration<Equipment> enumeration = equipment.elements();
        while (enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement());
        }
        return sb.toString();
    }
}
