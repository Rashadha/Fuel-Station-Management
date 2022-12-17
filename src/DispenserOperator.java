import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DispenserOperator
{
    public void pumpFuel(PreparedStatement psmt, Connection con, String query8, float dispensedFuelAmount, float paidAmount, Date dispensedDate, int queueEntryNumber)
    {
        try {
            psmt = con.prepareStatement(query8);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(1, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(2, "YES");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(3, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setFloat(4, dispensedFuelAmount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(5, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setFloat(6, paidAmount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(7, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setDate(8, dispensedDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setInt(9, queueEntryNumber);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Fuel pumped and remove from the queue");
    }
}
