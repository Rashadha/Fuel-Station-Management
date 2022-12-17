import com.sun.jdi.event.ThreadStartEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class MultiThreading  extends Random implements Runnable
{
    private PreparedStatement psmt;
    private Connection con;

    public MultiThreading(PreparedStatement psmt, Connection con, String query6, String fuelType, String query7, String query8, String query9, int dispenserNumber, int queueEntryNumber, ResultSet rs, boolean isARecord, Queue queue, DieselFuelDispenseManager dieselFuelDispenseManager, OctaneFuelDispenseManager octaneFuelDispenseManager, DateClass date, float fuelPrice, DispenserOperator dispenserOperator) {
        this.psmt = psmt;
        this.con = con;
        this.query6 = query6;
        this.fuelType = fuelType;
        this.query7 = query7;
        this.query8 = query8;
        this.query9 = query9;
        this.dispenserNumber = dispenserNumber;
        this.queueEntryNumber = queueEntryNumber;
        this.rs = rs;
        this.isARecord = isARecord;
        this.queue = queue;
        this.dieselFuelDispenseManager = dieselFuelDispenseManager;
        this.octaneFuelDispenseManager = octaneFuelDispenseManager;
        this.date = date;
        this.fuelPrice = fuelPrice;
        this.dispenserOperator = dispenserOperator;
    }

    private String query6, fuelType, query7, query8, query9;
    private int dispenserNumber, queueEntryNumber;
    private ResultSet rs;
    private boolean isARecord;
    private Queue queue;
    private DieselFuelDispenseManager dieselFuelDispenseManager;
    private OctaneFuelDispenseManager octaneFuelDispenseManager;
    private float fuelAmount, paidAmount; // don't get this as a parameter in constructor
    private final Scanner sc = new Scanner(System.in);
    private DateClass date;
    private float fuelPrice;
    private String date2; // no neeed to consider aas a parameter in constructor
    private final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-mm-yyyy");
    private java.util.Date date3; // no need in constructor parameter
    private java.sql.Date sqlDate; // no need in constructor parameter
    private DispenserOperator dispenserOperator;
    private float remainingFuelAmount; // no need to in cons.
    @Override
    public void run() {
        try {
            queue.peakForFirstVehicle(psmt,con, query6,dispenserNumber, rs, queueEntryNumber, fuelType, isARecord);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isARecord)
        {
            // check for the availibility
            if (fuelType == "petrol")
            {
                this.octaneFuelDispenseManager = new OctaneFuelDispenseManager(0);
                this.remainingFuelAmount = this.octaneFuelDispenseManager.checkForTheAvailability(psmt,con,query7,rs);
                if (this.remainingFuelAmount > 50)
                {
                    // enter fuel amout
                    this.fuelAmount = enterFuelAmount();
                    this.paidAmount = calculateThePaidAmount();
                    this.date = new DateClass();
                    this.date2 = this.date.displayTodayDate();
                    // convert to sql date
                    try {
                        this.date3 = sdf1.parse(date2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.sqlDate = new java.sql.Date(this.date3.getTime());
                    this.dispenserOperator = new DispenserOperator();
                    this.dispenserOperator.pumpFuel(psmt, con, query8, fuelAmount, paidAmount, sqlDate, queueEntryNumber);
                    // update repository
                    this.octaneFuelDispenseManager.updateTheRepository(psmt, con, query9, fuelAmount);
                }
                else
                {
                    System.out.println("Not enough petrol fuel in the repository");
                }
            }
            else
            {
                this.dieselFuelDispenseManager = new DieselFuelDispenseManager(0);
                this.remainingFuelAmount = this.octaneFuelDispenseManager.checkForTheAvailability(psmt,con,query7,rs);
                if (this.remainingFuelAmount > 50)
                {
                    this.fuelAmount = enterFuelAmount();
                    this.paidAmount = calculateThePaidAmount();
                    this.date = new DateClass();
                    this.date2 = this.date.displayTodayDate();
                    // convert to sql date
                    try {
                        this.date3 = sdf1.parse(date2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.sqlDate = new java.sql.Date(this.date3.getTime());
                    // pump fuel and update database
                    this.dispenserOperator = new DispenserOperator();
                    this.dispenserOperator.pumpFuel(psmt, con, query8, fuelAmount, paidAmount, sqlDate, queueEntryNumber);
                    // update repository
                    this.dieselFuelDispenseManager.updateTheRepository(psmt, con, query9, fuelAmount);
                }
                else
                {
                    System.out.println("Not enough diesel fuel in the repository");
                }
            }
        }
        else
        {
            System.out.println("No vehicles available in "+fuelType+" "+dispenserNumber+" queue");
        }
    }
    public float enterFuelAmount()
    {
        float fuelAmount = 0;
        boolean valid = true;
        do {
            try{
                System.out.print("Enter fuel amount: ");
                fuelAmount = sc.nextFloat();
            }
            catch (InputMismatchException e)
            {
                System.out.println("You entry is invalid");
                valid = false;
            }
        }while (!valid);
        return fuelAmount;
    }
    public float calculateThePaidAmount()
    {
        return this.paidAmount = this.fuelAmount * this.fuelPrice;
    }

//    public Date enter
}
