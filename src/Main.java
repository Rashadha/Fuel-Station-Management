import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main extends Thread{
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        boolean valid = true;
        int option = 0;
        String systemOperatorUsername = "";
        String systemOperatorPassword = "";
        Queue queue = null;
        int recordCount = 0;
        String[] vehicles = new String[10];
        int systemOperatorOption = 0;
        String vehicleNum = null;
        int dispenserNumber = 0;
        int numberOfPositions =0;
        String vehicleType = null;
        String fuelType = null;
        boolean isARecord = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        int ticketNumber = 0;
        CommonWaitingQueue waitingQueue = null;
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/fuel_management_system?autoReconnect=true&useSSL=false","root","rashadha@73");

        // to get all records in particular queue who is not still pumped
        String query2 = "SELECT ticket_num,vehicle_num" +
                "FROM dispenser_detail" +
                "WHERE dispenser_num = ? and fuel_type = ? and fuel_pumped_or_not = ?;";
        // query to get the vehicle that's not added to queue who received a ticket
        String query3 = "select ticket_num, vehicle_num, vehicle_type, fuel_type" +
                "from ticket_management_system" +
                "where add_to_queue_or_not = ? and rownum = 1;";
        // query to check if there is available vehicles in waiting list
        String query4 = "select queue_entry_num, vehicle_type," +
                "fuel_type from dispenser_detail" +
                "where dispenser_number = ? and rownum = 1;";
        // query to add vehicle to a queue which is in common waiting queue
        String query5 = "UPDATE dispenser_detail " +
                "SET dispenser_number = REPLACE(dispenser_number,?,?) " +
                "WHERE queue_entry_num = ?;";
        // query to take the first vehicle from the queue
        String query6 =  "select queue_entry_num, fuel_type " +
                "from dispenser_detail " +
               "where fuel_pumped_or_not = ? and  dispenser_number = ? and rownum = 1;";
        //
        int queueEntryNumber = 0;
        boolean isARecordInWaiting = false;
        // query to enter to the queue
        String queryToEnqueue = "insert into dispenser_detail" +
                "(ticket_num, vehicle_num, vehicle_type, fuel_type, dispenser_num)" +
                "values(?, ?, ?, ?);";
        // query to enter a vehicle to ticket_counter table which received a ticket
        String query = "insert into ticket_counter " +
                "(vehicle_num, vehicle_type, fuel_type)" +
                "values (?, ?, ?)";
        // query to check remain fuel amount
        String query7 = "SELECT available_amount" +
                " FROM fuel_availability " +
                " WHERE fuel_type = ?;";
        String queryToReadObject = "select vehicle_num, vehicle_type, fuel_type" +
                "from ticket_management_system" +
                "where add_to_queue_or_not = ? and rownum = 1;";
        DieselFuelDispenseManager dieselFuelDispenseManager = null;
        OctaneFuelDispenseManager octaneFuelDispenseManager = null;
        DateClass date = null;
        DispenserOperator dispenserOperator = null;
        MultiThreading thread = null;
        // query to update dispensed vehicle detail
        String query8 = "UPDATE dispenser_detail " +
                "SET fuel_pumped_or_not = REPLACE(fuel_pumped_or_not,?,?), " +
                "dispensed_fuel_amount = REPLACE(dispensed_fuel_amount, ?, ?), " +
                "paid_amount = REPLACE(paid_amount,?,?), " +
                "dispensed_date = REPLACE(dispensed_date,?,?) " +
                "WHERE queue_entry_num = ?;";
        // query to add to common waiting queue who received a ticket if there is no availabe position in particular queue
        String queryToAddToCommonWaitingQueue = "insert into " +
                "dispenser_detail " +
                "(ticket_num, vehicle_num, vehicle_type, fuel_type, " +
                "dispenser_num) " +
                "values (?, ?, ?, ?, ?);";
        String query9 = "UPDATE fuel_availability " +
                "SET available_amount = ? " +
                "WHERE fuel_type = ?;";
        Scanner sc = new Scanner(System.in);
        do
        {
            System.out.println("input according to the option: "); // 1 --> customer, 2--> system operator
            
            try
            {
                option = sc.nextInt();
                if (!(option <= 6 && option >= 1))
                {
                    throw new IllegalArgumentException();
                }    
            } 
            catch(IllegalArgumentException e)
            {
                System.out.println("Your input is invalid");
                valid = false;
            }
            catch(InputMismatchException e)
            {
                System.out.println("You input is invalid");
            }    
        }while (!valid);
        
        if (option == 1)
        {
            // to display number of available positions in queue
            psmt = con.prepareStatement(query2);
            for (int i = 0; i<4; i++)
            {
                psmt.setInt(1,i+1);
                psmt.setString(2, "petrol");
                psmt.setString(3,null);
                rs = psmt.executeQuery();

                while (rs.next())
                {
                    vehicleNum = rs.getString(3);
                    vehicles[recordCount] = vehicleNum;
                    recordCount+=1;
                }
                con.close();
                queue = new Queue(waitingQueue,vehicles,0);
                queue.displayNumberOfPositions(fuelType,i+1);
            }

            recordCount = 0;
            for (int i = 0; i<3; i++)
            {
                psmt.setInt(1,i+1);
                psmt.setString(2, "diesel");
                psmt.setString(3,null);
                rs = psmt.executeQuery();

                while (rs.next())
                {
                    vehicleNum = rs.getString(3);
                    vehicles[recordCount] = vehicleNum;
                    recordCount+=1;
                }
                con.close();
                queue = new Queue(waitingQueue,vehicles,0);
                queue.displayNumberOfPositions(fuelType,i+1);
            }
            System.out.print("Enter vehicle Number: ");
            vehicleNum = sc.next();
            System.out.println("Enter vehicle type (eg: \"car\" or " +
                    "\"van\" or \"motor bike\" or \"three wheeler\"" +
                    " or \"public transport\" or \"other\"): ");
            do{
                try
                {
                    vehicleType = sc.next().toLowerCase();
                    if (!(vehicleType == "car" || vehicleType == "van"
                     || vehicleType == "motor bike" || vehicleType == "three wheeler"
                    || vehicleType == "public transport" || vehicleType == "other"))
                    {
                        throw new IllegalArgumentException();
                    }
                }
                catch (IllegalArgumentException e)
                {
                    System.out.println("Your entry is invalid");
                    valid = false;
                }
            }while (!valid);


            System.out.println("Enter fuel type(eg: \"92Octane\" or \"Diesel\"): ");
            do {
                try
                {
                    fuelType = sc.next();
                    if (fuelType == "92Octane" || fuelType == "Diesel")
                        throw new IllegalArgumentException();
                }
                catch(IllegalArgumentException e)
                {
                    System.out.println("Your entry is invalid");
                    valid = false;
                }
            }while (!valid);

            Customer customer = new Customer(vehicleNum,vehicleType,fuelType);
            TicketCounter ticketCounter = new TicketCounter();
            ticketCounter.issueTicket(customer);
            ticketCounter.saveCustomerDetails(con,query);
        }
        if (option == 2) // system operator
        {
           do{
               System.out.print("Enter the username: ");
               try{
                   systemOperatorUsername = sc.next();
                   System.out.print("Enter the password: ");
                   systemOperatorPassword = sc.next();
                   if (!(systemOperatorUsername =="groupActivity" && systemOperatorPassword == "dudupi"))
                   {
                       throw new IllegalArgumentException();
                   }
               }
               catch (IllegalArgumentException e)
               {
                   System.out.println("Your entry is invalid");
               }
           }while (!(systemOperatorUsername =="rashadha" && systemOperatorPassword == "dudupi"));

           do{
               System.out.print("Enter an option: "); // 1--> enQueue

               try
               {
                   systemOperatorOption = sc.nextInt();
                   if (systemOperatorOption <= 6 && systemOperatorOption >= 1)
                       throw new IllegalArgumentException();
               }
               catch (IllegalArgumentException e)
               {
                   System.out.println("Your entry is invalid");
                   valid = false;
               }
               catch (InputMismatchException e)
               {
                   System.out.println("Your entry is invalid");
               }
           }while (!valid);
           vehicles = new String[10];

           if (systemOperatorOption == 1) // add to queue
           {
               // check in the common waiting queue, if not check in the ticket counter
               psmt = con.prepareStatement(query4);
               psmt.setInt(1, 0);
               rs = psmt.executeQuery();

               if (rs.next())
               {
                   isARecordInWaiting = true;
                   queueEntryNumber = rs.getInt(1);
                   vehicleType = rs.getString(4);
                   fuelType = rs.getString(5);
               }
               con.close();

               if (isARecordInWaiting)
               {
                   if (fuelType == "diesel") {
                       if (vehicleType == "public transport") {
                           // 1
                           dispenserNumber = 1;

                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() != 0) {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }
                       } else // 2,3
                       {
                           dispenserNumber = 2;

                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() == 0) {
                               dispenserNumber = 3;
                               recordCount = 0;
                               vehicles = new String[10];

                               queue = new Queue(waitingQueue, vehicles, 0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                               if (queue.getNumOfPositions() != 0) {
                                   waitingQueue = new CommonWaitingQueue();
                                   waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                               }
                           } else {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }
                       }
                   }
                   else if (fuelType == "petrol")
                   {
                       if (vehicleType == "car" || vehicleType == "van")
                       {
                           // 1 or 2
                           dispenserNumber = 1;


                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() == 0) {
                               dispenserNumber = 2;
                               recordCount = 0;
                               vehicles = new String[10];

                               queue = new Queue(waitingQueue, vehicles, 0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                               if (queue.getNumOfPositions() != 0) {
                                   waitingQueue = new CommonWaitingQueue();
                                   waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                               }
                           } else {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }

                       }
                       else if (vehicleType == "three wheeler")
                       {
                           // 3
                           dispenserNumber = 3;
                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() != 0) {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }
                       }
                       else if (vehicleType == "motor bike")
                       {
                           // 4
                           dispenserNumber = 4;
                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() != 0) {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }
                       }
                       else
                       {
                           // 2
                           dispenserNumber = 2;
                           queue = new Queue(waitingQueue, vehicles, 0);
                           queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 1);
                           if (queue.getNumOfPositions() != 0) {
                               waitingQueue = new CommonWaitingQueue();
                               waitingQueue.removeFromCommonWaitingQueue(psmt, con, query5, dispenserNumber, queueEntryNumber);
                           }
                       }

                   }

               }

               else
               {
                   // check in ticket counter
                   psmt = con.prepareStatement(query3);
                   psmt.setString(1,null);
                   rs = psmt.executeQuery();

                   if (rs.next())
                   {
                       isARecord = true;
                       ticketNumber = rs.getInt(1);
                       vehicleNum = rs.getString(2);
                       vehicleType = rs.getNString(3);
                       fuelType = rs.getString(4);
                   }
                   con.close();
                   recordCount = 0;
                   if (!isARecord)
                   {
                       if (fuelType == "diesel")
                       {
                           if (vehicleType == "public transport")
                           {
                               // 1
                               dispenserNumber = 1;

                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                           }
                           else // 2,3
                           {
                               dispenserNumber = 2;

                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                               numberOfPositions = queue.getNumOfPositions();
                               if (numberOfPositions == 0)
                               {
                                   dispenserNumber = 3;
                                   recordCount = 0;
                                   vehicles  = new String[10];

                                   queue = new Queue(waitingQueue,vehicles,0);
                                   queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                               }
                           }
                       }
                       else if (fuelType == "petrol")
                       {
                           if (vehicleType == "car" || vehicleType == "van")
                           {
                               // 1 or 2
                               dispenserNumber = 1;
                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                               numberOfPositions = queue.getNumOfPositions();

                               if (numberOfPositions == 0)
                               {
                                   // add to common waiting queue
                                   dispenserNumber = 3;
                                   recordCount = 0;
                                   vehicles  = new String[10];

                                   queue = new Queue(waitingQueue,vehicles,0);
                                   queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);

                               }

                           }
                           else if (vehicleType == "three wheeler")
                           {
                               // 3
                               dispenserNumber = 3;
                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);

                           }
                           else if (vehicleType == "motor bike")
                           {
                               // 4
                               dispenserNumber = 4;
                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                           }
                           else
                           {
                               // 2
                               dispenserNumber = 2;
                               queue = new Queue(waitingQueue,vehicles,0);
                               queue.isFull(psmt, con, query2, dispenserNumber, fuelType, vehicleNum, recordCount, ticketNumber, rs, queryToEnqueue, vehicleType, queryToAddToCommonWaitingQueue, 0);
                           }

                       }
                   }
               }
           }
           //
        }
        if (option == 3) // dispense operator
        {
//            MultiThreading thread1 = new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,1,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator);
//            MultiThreading thread2 = new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,2,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator);
//            MultiThreading thread3 = new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,3,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator);
//            MultiThreading thread4 = new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,4,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator);
//            MultiThreading thread5 = new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,1,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator);
//            MultiThreading thread6 = new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,2,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator);
//            MultiThreading thread7 = new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,3,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator);

            new Thread(new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,1,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,2,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,3,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"petrol",query7,query8,query9,4,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,450,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,1,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,2,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator));
            new Thread(new MultiThreading(psmt,con,query6,"diesel",query7,query8,query9,3,queueEntryNumber,rs,isARecord,queue,dieselFuelDispenseManager,octaneFuelDispenseManager,date,430,dispenserOperator));
        }
    }
    @Override
    public  void run()
    {

    }
}