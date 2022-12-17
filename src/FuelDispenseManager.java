import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface FuelDispenseManager
{
    public float checkForTheAvailability(PreparedStatement psmt, Connection con, String query7, ResultSet rs);
    public void updateTheRepository(PreparedStatement psmt, Connection con, String query9, float fuelAmount);
    public void installDispenser();
    public void printEachDayStationDetails();
    public void printLargestAmountDispensedVehicle();
    public void printDispenserWisedDetails();
    public void stockFuelInDispenser();
}
