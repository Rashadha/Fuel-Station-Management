import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OctaneFuelDispenseManager implements FuelDispenseManager{

    public OctaneFuelDispenseManager(float remaining92OctaneAmount) {
        this.remaining92OctaneAmount = remaining92OctaneAmount;
    }

    public float getRemaining92OctaneAmount() {
        return remaining92OctaneAmount;
    }

    public void setRemaining92OctaneAmount(float remaining92OctaneAmount) {
        this.remaining92OctaneAmount = remaining92OctaneAmount;
    }

    private float remaining92OctaneAmount;
    @Override
    public float checkForTheAvailability(PreparedStatement psmt, Connection con, String query7, ResultSet rs) {
        try {
            psmt = con.prepareStatement(query7);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(1, "92Octane");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            rs = psmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (rs.next())
            {
                this.remaining92OctaneAmount = rs.getFloat(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this.remaining92OctaneAmount;
    }

    @Override
    public void updateTheRepository(PreparedStatement psmt, Connection con, String query9, float fuelAmount)
    {
        try {
            psmt = con.prepareStatement(query9);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setFloat(1, this.remaining92OctaneAmount - fuelAmount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            psmt.setString(2, "92Octane");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("92Octane Repository is updated");
    }

    @Override
    public void installDispenser() {

    }

    @Override
    public void printEachDayStationDetails() {

    }

    @Override
    public void printLargestAmountDispensedVehicle() {

    }

    @Override
    public void printDispenserWisedDetails() {

    }

    @Override
    public void stockFuelInDispenser() {

    }

}
