package Ayrotek.demo.service;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.dto.RegistrationRequest.*;
import Ayrotek.demo.entity.*;
import Ayrotek.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Service
public class RegistrationService {

    private final PersonRepository personRepository;

    public RegistrationService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person processRegistration(RegistrationRequest request) {
        // --- VALIDATION ---
        if (request == null || request.driversLicense == null || request.vehicleRegistration == null) {
            throw new IllegalArgumentException("The registration request is incomplete.");
        }
        HolderDTO holderDto = request.driversLicense.holder;
        if (holderDto == null || holderDto.nationalId == null) {
            throw new IllegalArgumentException("Driver's license holder information with national ID is required.");
        }
        VehicleDTO vehicleDto = request.vehicleRegistration.vehicle;
        if (vehicleDto == null || vehicleDto.getPlateNumber() == null) {
            throw new IllegalArgumentException("Vehicle information with a plate number is required.");
        }

        // --- FIND OR CREATE PERSON ---
        Person person = personRepository.findByNationalId(holderDto.nationalId)
                .orElseGet(() -> {
                    Person newPerson = new Person();
                    newPerson.setNationalId(holderDto.nationalId);
                    newPerson.setFirstName(holderDto.firstName);
                    newPerson.setLastName(holderDto.lastName);
                    newPerson.setDateOfBirth(holderDto.dateOfBirth);
                    return newPerson;
                });

        // --- IDEMPOTENCY CHECK: See if this vehicle is already registered to this person ---
        boolean vehicleExists = person.getVehicles().stream()
                .anyMatch(v -> v.getPlateNumber().equals(vehicleDto.getPlateNumber()));

        if (vehicleExists) {
            // If the vehicle already exists, do not create a duplicate.
            // Simply return the existing person record.
            return person;
        }
        // --- END OF IDEMPOTENCY CHECK ---


        // --- MAP AND LINK NEW VEHICLE (only if it doesn't exist) ---
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vehicleDto.vin);
        vehicle.setPlateNumber(vehicleDto.plateNumber);
        vehicle.setMake(vehicleDto.make);
        vehicle.setModel(vehicleDto.model);
        vehicle.setYear(vehicleDto.year);
        vehicle.setColor(vehicleDto.color);
        vehicle.setEngineNumber(vehicleDto.engineNumber);
        vehicle.setFuelType(vehicleDto.fuelType);
        vehicle.setOwner(person);

        VehicleRegistration registration = new VehicleRegistration();
        registration.setRegistrationNumber(request.vehicleRegistration.registrationNumber);
        registration.setIssueDate(request.vehicleRegistration.issueDate);
        registration.setExpiryDate(request.vehicleRegistration.expiryDate);
        registration.setVehicle(vehicle);
        vehicle.setRegistration(registration);

        person.getVehicles().add(vehicle);

        // --- MAP AND LINK LICENSE (only if the person is new) ---
        if (person.getId() == null) {
            DriversLicense license = new DriversLicense();
            license.setLicenseNumber(request.driversLicense.licenseNumber);
            license.setIssueDate(request.driversLicense.issueDate);
            license.setExpiryDate(request.driversLicense.expiryDate);
            license.setCategories(request.driversLicense.categories);
            license.setPerson(person);
            person.setDriversLicense(license);
        }

        // --- SAVE ---
        return personRepository.save(person);
    }
}