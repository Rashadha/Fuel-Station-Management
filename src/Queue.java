import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queue
{
    public Queue(CommonWaitingQueue waitingQueue, String[] vehicleQueue, int numOfPositions) {
        this.waitingQueue = waitingQueue;
        this.vehicleQueue = vehicleQueue;
        this.numOfPositions = numOfPositions;
    }

    public CommonWaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public void setWaitingQueue(CommonWaitingQueue waitingQueue) {
        this.waitingQueue = waitingQueue;
    }

    //    public Queue(String[] vehicleQueue) {
//        this.vehicleQueue = vehicleQueue;
//    }
    private CommonWaitingQueue waitingQueue;

    public String[] getVehicleQueue() {
        return vehicleQueue;
    }

    public void setVehicleQueue(String[] vehicleQueue) {
        this.vehicleQueue = vehicleQueue;
    }

    private String[] vehicleQueue = new String[10];

//    public Queue(String[] vehicleQueue, int numOfPositions) {
//        this.vehicleQueue = vehicleQueue;
//        this.numOfPositions = numOfPositions;
//    }

    public int getNumOfPositions() {
        return numOfPositions;
    }

    public void setNumOfPositions(int numOfPositions) {
        this.numOfPositions = numOfPositions;
    }

    private int numOfPositions;

    public void displayNumberOfPositions(String fuelType,int dispenserNumber)
    {
        for (int i = 0; i<this.vehicleQueue.length; i++)
        {
            if (this.vehicleQueue[i]==null)
            {
                this.numOfPositions += 1;
            }
        }
        if (fuelType == "petrol")
        {
            System.out.println("No of remaining positions in petrol dispenser "+dispenserNumber+" = "
                    +this.numOfPositions);
        }
        else
        {
            System.out.println("No of remaining positions in diesel dispenser "+dispenserNumber+" = "
                    +this.numOfPositions);
        }
    }
    public void isFull(PreparedStatement psmt, Connection con, String query2, int dispenserNumber, String fuelType, String vehicleNum, int recordCount
    , int ticketNumber, ResultSet rs, String queryToEnqueue, String vehicleType, String queryToAddToCommonWaitingQueue, int fromWaiting) throws SQLException {
        psmt = con.prepareStatement(query2);
        psmt.setInt(1,dispenserNumber);
        psmt.setString(2, fuelType);
        psmt.setString(3,null);
        rs = psmt.executeQuery();

        while (rs.next())
        {
            vehicleNum = rs.getString(3);
            ticketNumber = rs.getInt(1);
            this.vehicleQueue[recordCount] = vehicleNum;
            recordCount+=1;
        }
        con.close();
        displayNumberOfPositions(fuelType,dispenserNumber);
        if (fromWaiting == 0)
        {
            if (numOfPositions == 0)
            {
                if (fuelType == "diesel" && dispenserNumber == 2)
                {
                    return;
                }
                else if (fuelType == "petrol" && dispenserNumber == 1)
                {
                    return;
                }
                this.waitingQueue = new CommonWaitingQueue();
                this.waitingQueue.addToCommonQueue(psmt, con, queryToAddToCommonWaitingQueue, ticketNumber, vehicleNum, vehicleType, fuelType);
            }
            else
                addToQueue(psmt, con, queryToEnqueue, ticketNumber, vehicleNum, vehicleType, fuelType, dispenserNumber);
        }
    }
    public void addToQueue(PreparedStatement psmt, Connection con, String queryToEnqueue, int ticketNumber,
                           String vehicleNum, String vehicleType, String fuelType, int dispenserNumber) throws SQLException {
        psmt = con.prepareStatement(queryToEnqueue);
        psmt.setInt(1, ticketNumber);
        psmt.setString(2, vehicleNum);
        psmt.setString(3,vehicleType);
        psmt.setString(4, fuelType);
        psmt.setInt(5, dispenserNumber);
        psmt.executeQuery();
        con.close();
        System.out.println("added to "+fuelType+" dispenser queue "+dispenserNumber);
    }

    public boolean peakForFirstVehicle(PreparedStatement psmt, Connection con, String query6, int dispenserNumber, ResultSet rs, int queueEntryNumber, String fuelType, boolean isARecord) throws SQLException {
        psmt = con.prepareStatement(query6);
        psmt.setString(1, null);
        psmt.setInt(2, dispenserNumber);
        rs = psmt.executeQuery();

        if (rs.next())
        {
            queueEntryNumber = rs.getInt(1);
            fuelType = rs.getString(5);
            isARecord = true;
        }
        con.close();
        return isARecord;
    }
}
