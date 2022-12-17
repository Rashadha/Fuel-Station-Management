import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class TicketCounter
{



    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    ArrayList<Customer> customers = new ArrayList<>();
    public void saveCustomerDetails(Connection con, String query) throws ClassNotFoundException, SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, customers.get(customers.size()-1).getVehicleNum());
        stmt.setString(2, customers.get(customers.size()-1).getVehicleType());
        stmt.setString(3, customers.get(customers.size()-1).getFuelType());

        stmt.execute();
        con.close();
    }

    public void issueTicket(Customer customer)
    {
        customers.add(customer);
    }
}
