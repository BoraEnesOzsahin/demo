package Ayrotek.demo.service;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.dto.RegistrationRequest.*;
import Ayrotek.demo.entity.DriversLicense;
import Ayrotek.demo.entity.Person;
import Ayrotek.demo.entity.Vehicle;
import Ayrotek.demo.entity.VehicleRegistration;
import Ayrotek.demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Objects;

@Service
public class VerificationService {

    private final PersonRepository personRepository;

    public VerificationService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Verifies the entire registration request against stored data.
     * @param request The verification data.
     * @return A string describing the first mismatch found, or null if everything matches.
     */
    public String verifyAndGetMismatchReason(RegistrationRequest request) {
        HolderDTO holderDto = request.driversLicense.holder;
        VehicleDTO vehicleDto = request.vehicleRegistration.vehicle;

        Person personInDb = personRepository.findByNationalId(holderDto.nationalId)
                .orElseThrow(() -> new EntityNotFoundException("No person found with national ID " + holderDto.nationalId));

        // Person details
        if (!Objects.equals(personInDb.getFirstName(), holderDto.firstName)) return "First name mismatch";
        if (!Objects.equals(personInDb.getLastName(), holderDto.lastName)) return "Last name mismatch";
        if (!Objects.equals(personInDb.getDateOfBirth(), holderDto.dateOfBirth)) return "Date of birth mismatch";

        // License details
        DriversLicense licenseInDb = personInDb.getDriversLicense();
        if (!Objects.equals(licenseInDb.getLicenseNumber(), request.driversLicense.licenseNumber)) return "License number mismatch";
        if (!Objects.equals(licenseInDb.getIssueDate(), request.driversLicense.issueDate)) return "License issue date mismatch";
        if (!Objects.equals(licenseInDb.getExpiryDate(), request.driversLicense.expiryDate)) return "License expiry date mismatch";
        if (!new HashSet<>(licenseInDb.getCategories()).equals(new HashSet<>(request.driversLicense.categories))) return "License categories mismatch";

        // Find the specific vehicle
        Vehicle vehicleInDb = personInDb.getVehicles().stream()
                .filter(v -> vehicleDto.plateNumber.equals(v.getPlateNumber()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No vehicle with plate number " + vehicleDto.plateNumber + " found for this person."));

        // Vehicle details
        if (!Objects.equals(vehicleInDb.getMake(), vehicleDto.make)) return "Vehicle make mismatch";
        if (!Objects.equals(vehicleInDb.getModel(), vehicleDto.model)) return "Vehicle model mismatch";
        if (vehicleInDb.getYear() != vehicleDto.year) return "Vehicle year mismatch";
        if (!Objects.equals(vehicleInDb.getColor(), vehicleDto.color)) return "Vehicle color mismatch";
        if (!Objects.equals(vehicleInDb.getVin(), vehicleDto.vin)) return "Vehicle VIN mismatch";
        if (!Objects.equals(vehicleInDb.getEngineNumber(), vehicleDto.engineNumber)) return "Vehicle engine number mismatch";
        if (!Objects.equals(vehicleInDb.getFuelType(), vehicleDto.fuelType)) return "Vehicle fuel type mismatch";
        if (vehicleInDb.getVehicleType() == null || vehicleDto.vehicleType == null) return "Vehicle type missing";
        if (!Objects.equals(vehicleInDb.getVehicleType().getId(), vehicleDto.vehicleType.getId())) return "Vehicle type ID mismatch";
        if (!Objects.equals(vehicleInDb.getVehicleType().getVehicleType(), vehicleDto.vehicleType.getVehicleType())) return "Vehicle type mismatch";
        if (!Objects.equals(vehicleInDb.getCompany(), vehicleDto.company)) return "Company name mismatch";

        // Vehicle Registration details
        VehicleRegistration registrationInDb = vehicleInDb.getRegistration();
        if (!Objects.equals(registrationInDb.getRegistrationNumber(), request.vehicleRegistration.registrationNumber)) return "Vehicle registration number mismatch";
        if (!Objects.equals(registrationInDb.getIssueDate(), request.vehicleRegistration.issueDate)) return "Vehicle registration issue date mismatch";
        if (!Objects.equals(registrationInDb.getExpiryDate(), request.vehicleRegistration.expiryDate)) return "Vehicle registration expiry date mismatch";

        return null; // All checks passed
    }


}