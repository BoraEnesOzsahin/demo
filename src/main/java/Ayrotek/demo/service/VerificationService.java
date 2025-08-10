package Ayrotek.demo.service;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.dto.RegistrationRequest.*;
import Ayrotek.demo.entity.DriversLicense;
import Ayrotek.demo.entity.Person;
import Ayrotek.demo.entity.Vehicle;
import Ayrotek.demo.entity.VehicleRegistration;
import Ayrotek.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Service
public class VerificationService {

    private final PersonRepository personRepository;

    public VerificationService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Verifies the entire registration request against stored data.
     * Every field must match exactly.
     * @param request The verification data, in the same format as a registration.
     * @return true only if all provided data matches the database record.
     */
    public boolean verifyRegistrationData(RegistrationRequest request) {
        // --- Step 1: Basic validation and get key identifiers ---
        if (request == null || request.driversLicense == null || request.driversLicense.holder == null || request.vehicleRegistration == null || request.vehicleRegistration.vehicle == null) {
            return false; // Invalid request structure
        }
        HolderDTO holderDto = request.driversLicense.holder;
        VehicleDTO vehicleDto = request.vehicleRegistration.vehicle;
        if (holderDto.nationalId == null || vehicleDto.plateNumber == null) {
            return false; // Cannot verify without nationalId and plateNumber
        }

        // --- Step 2: Find the Person by National ID ---
        Optional<Person> personOpt = personRepository.findByNationalId(holderDto.nationalId);
        if (personOpt.isEmpty()) {
            return false; // Person not found, verification fails
        }
        Person personInDb = personOpt.get();

        // --- Step 3: Verify Person's details ---
        if (!Objects.equals(personInDb.getFirstName(), holderDto.firstName) ||
            !Objects.equals(personInDb.getLastName(), holderDto.lastName) ||
            !Objects.equals(personInDb.getDateOfBirth(), holderDto.dateOfBirth)) {
            return false;
        }

        // --- Step 4: Verify Driver's License details ---
        DriversLicense licenseInDb = personInDb.getDriversLicense();
        DriversLicenseDTO licenseDto = request.driversLicense;
        if (licenseInDb == null) return false; // No license in DB to compare against

        if (!Objects.equals(licenseInDb.getLicenseNumber(), licenseDto.licenseNumber) ||
            !Objects.equals(licenseInDb.getIssueDate(), licenseDto.issueDate) ||
            !Objects.equals(licenseInDb.getExpiryDate(), licenseDto.expiryDate) ||
            !Objects.equals(new HashSet<>(licenseInDb.getCategories()), new HashSet<>(licenseDto.categories))) {
            return false;
        }

        // --- Step 5: Find the specific Vehicle by plate number ---
        Optional<Vehicle> vehicleOpt = personInDb.getVehicles().stream()
                .filter(v -> vehicleDto.plateNumber.equals(v.getPlateNumber()))
                .findFirst();
        if (vehicleOpt.isEmpty()) {
            return false; // Vehicle with this plate number not found for this person
        }
        Vehicle vehicleInDb = vehicleOpt.get();

        // --- Step 6: Verify Vehicle details ---
        if (!Objects.equals(vehicleInDb.getMake(), vehicleDto.make) ||
            !Objects.equals(vehicleInDb.getModel(), vehicleDto.model) ||
            vehicleInDb.getYear() != vehicleDto.year ||
            !Objects.equals(vehicleInDb.getColor(), vehicleDto.color) ||
            !Objects.equals(vehicleInDb.getVin(), vehicleDto.vin) ||
            !Objects.equals(vehicleInDb.getEngineNumber(), vehicleDto.engineNumber) ||
            !Objects.equals(vehicleInDb.getFuelType(), vehicleDto.fuelType)) {
            return false;
        }

        // --- Step 7: Verify Vehicle Registration details ---
        VehicleRegistration registrationInDb = vehicleInDb.getRegistration();
        VehicleRegistrationDTO regDto = request.vehicleRegistration;
        if (registrationInDb == null) return false; // No registration in DB to compare against

        if (!Objects.equals(registrationInDb.getRegistrationNumber(), regDto.registrationNumber) ||
            !Objects.equals(registrationInDb.getIssueDate(), regDto.issueDate) ||
            !Objects.equals(registrationInDb.getExpiryDate(), regDto.expiryDate)) {
            return false;
        }

        // --- Step 8: If all checks passed, verification is successful ---
        return true;
    }
}