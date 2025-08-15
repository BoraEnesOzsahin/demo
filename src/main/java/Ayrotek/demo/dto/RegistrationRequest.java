package Ayrotek.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class RegistrationRequest {
    public VehicleRegistrationDTO vehicleRegistration;
    public DriversLicenseDTO driversLicense;
    public String regCode;
    public String adminPassword;

    public static class VehicleRegistrationDTO {
        public String registrationNumber;
        public LocalDate issueDate;
        public LocalDate expiryDate;
        public OwnerDTO owner;
        public VehicleDTO vehicle;
    }

    public static class DriversLicenseDTO {
        public String licenseNumber;
        public LocalDate issueDate;
        public LocalDate expiryDate;
        public List<String> categories;
        public HolderDTO holder;
    }

    public static class OwnerDTO {
        public String nationalId;
    }

    public static class HolderDTO {
        public String firstName;
        public String lastName;
        public LocalDate dateOfBirth;
        public String nationalId;
    }

    public static class VehicleDTO {
        public String make;
        public String model;
        public int year;
        public String color;
        public String vin;
        public String engineNumber;
        public String plateNumber;
        public String fuelType;
        public String vehicleType;//Personal or Commercial
        public String company; // Only for commercial vehicles


        public String getPlateNumber() {
                return plateNumber;
            }
    }
}