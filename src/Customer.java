public class Customer
{
    public Customer(String vehicleNum, String vehicleType, String fuelType) {
        this.vehicleNum = vehicleNum;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
    }

    public String getVehicleNum() {
        return vehicleNum;
    }

    public void setVehicleNum(String vehicleNum) {
        this.vehicleNum = vehicleNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    private String vehicleNum;
    private String vehicleType;
    private String fuelType;
}
