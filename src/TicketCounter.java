import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class TicketCounter
{
    private String fuelType;
    private double fuelAmount;
    private String vehicleNumber;
    private String vehicleType;
    private String dispenserNumber;
    private String ticketNumber;
    private DateClass date;
    private String[] vehicleQueue;
    private CommonWaitingQueue waitingQueue;
    private Queue vehicleList = new Queue(waitingQueue, vehicleQueue,10);

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDispenserNumber() {
        return dispenserNumber;
    }

    public void setDispenserNumber(String dispenserNumber) {
        this.dispenserNumber = dispenserNumber;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public DateClass getDate() {
        return date;
    }

    public void setDate(DateClass date) {
        this.date = date;
    }

    public Queue getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(Queue vehicleList) {
        this.vehicleList = vehicleList;
    }

    public TicketCounter() {
    }

    public void issueTicket() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter the vehicle number: ");
        setVehicleNumber(input.nextLine());

        System.out.println("Fuel type:");
        System.out.println("1 --> 92 Octane");
        System.out.println("2 --> Diesel");

        int fuelTypeNumber = input.nextInt();

        switch (fuelTypeNumber) {
            case 1:
                setFuelType("92 Octane");
                allocateDispenser("92 Octane");
                break;
            case 2:
                setFuelType("Diesel");
                allocateDispenser("Diesel");
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid input.");
        }

        System.out.println("Enter the fuel amount: ");
        setFuelAmount(input.nextDouble());
    }

    private void allocateDispenser(String fuelType) {

        Scanner input = new Scanner(System.in);
        if (fuelType.equals("92 Octane")) {
            System.out.println("Vehicle Type: ");
            System.out.println("1 --> Car");
            System.out.println("2 --> Van");
            System.out.println("3 --> Three wheeler");
            System.out.println("4 --> Motor bike");
            System.out.println("5 --> Other vehicle");

            int vehicleTypeNumber = input.nextInt();

            switch (vehicleTypeNumber) {
                case 1:
                    setVehicleType("Car");
                    setDispenserNumber("P1 and P2");
                    break;
                case 2:
                    setVehicleType("Van");
                    setDispenserNumber("P1 and P2");
                    break;
                case 3:
                    setVehicleType("Three wheeler");
                    setDispenserNumber("P3");
                    break;
                case 4:
                    setVehicleType("Motor bike");
                    setDispenserNumber("P4");
                    break;
                case 5:
                    setVehicleType("Other vehicle");
                    setDispenserNumber("P2");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        } else if (fuelType.equals("Diesel")) {
            System.out.println("Vehicle Type: ");
            System.out.println("1 -->  Public transport vehicles");
            System.out.println("2 --> Other vehicles");

            int vehicleTypeNumber = input.nextInt();

            switch (vehicleTypeNumber) {
                case 1:
                    setVehicleType("Public transport vehicles");
                    setDispenserNumber("D1");
                    break;
                case 2:
                    setVehicleType("Other vehicles");
                    setDispenserNumber("D2 and D3");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
        ticket();
        

    }

    public void ticket() {
        for (int i = 0; i < 1; i++) {
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            System.out.println("*   -------------------               --------------    *");
            System.out.println("*         " + getVehicleNumber() + "                            " + getDate() + "          *");
            System.out.println("*   -------------------               --------------    *");
            System.out.println("*              --------------------------               *");
            System.out.println("*            " + getFuelType() + " Dispenser " + getDispenserNumber() + "     *");
            System.out.println("*                  Ticket Number: " + getTicketNumber() + "                  *");
            System.out.println("*              --------------------------               *");
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
        }
    }



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
