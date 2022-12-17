import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommonWaitingQueue
{
    private ArrayList<String> commonQueue = new ArrayList<>();

    public void addToCommonQueue(PreparedStatement psmt, Connection con, String queryToAddToCommonWaitingQueue,
                                 int ticketNumber, String vehicleNum, String vehicleType, String fuelType) throws SQLException {
        psmt = con.prepareStatement(queryToAddToCommonWaitingQueue);
        psmt.setInt(1, ticketNumber);
        psmt.setString(2, vehicleNum);
        psmt.setString(3,vehicleType);
        psmt.setString(4, fuelType);
        psmt.setInt(5, 0);
        psmt.executeQuery();
        con.close();
        System.out.println("Added to common waiting queue");
    }
    public void removeFromCommonWaitingQueue(PreparedStatement psmt, Connection con, String query5,
                                             int dispenserNum, int queueEntryNumber) throws SQLException {
        psmt = con.prepareStatement(query5);
        psmt.setInt(1,0);
        psmt.setInt(2,dispenserNum);
        psmt.setInt(3, queueEntryNumber);
        psmt.executeQuery();
        con.close();
        System.out.println("Vehicle is removed from a waiting queue");
    }
}
