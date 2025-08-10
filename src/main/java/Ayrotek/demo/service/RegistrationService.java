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
        // --- VALIDATION: Ensure the request and its critical parts are not null ---
        if (request == null || request.driversLicense == null || request.vehicleRegistration == null) {
            throw new IllegalArgumentException("The registration request is incomplete.");
        }
        HolderDTO holderDto = request.driversLicense.holder;
        if (holderDto == null || holderDto.nationalId == null) {
            throw new IllegalArgumentException("Driver's license holder information with national ID is required.");
        }
        VehicleDTO vehicleDto = request.vehicleRegistration.vehicle;
        if (vehicleDto == null) {
            throw new IllegalArgumentException("Vehicle information is missing.");
        }

        // --- FIND OR CREATE PERSON ---
        // This logic correctly finds an existing person or prepares a new one.
        Person person = personRepository.findByNationalId(holderDto.nationalId)
                .orElseGet(() -> {
                    Person newPerson = new Person();
                    newPerson.setNationalId(holderDto.nationalId);
                    newPerson.setFirstName(holderDto.firstName);
                    newPerson.setLastName(holderDto.lastName);
                    newPerson.setDateOfBirth(holderDto.dateOfBirth);
                    return newPerson;
                });

        // --- MAP AND LINK VEHICLE ---
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vehicleDto.vin);
        vehicle.setPlateNumber(vehicleDto.plateNumber);
        vehicle.setMake(vehicleDto.make);
        vehicle.setModel(vehicleDto.model);
        vehicle.setYear(vehicleDto.year);
        vehicle.setColor(vehicleDto.color);
        vehicle.setEngineNumber(vehicleDto.engineNumber);
        vehicle.setFuelType(vehicleDto.fuelType);
        vehicle.setOwner(person); // Link vehicle to person

        VehicleRegistration registration = new VehicleRegistration();
        registration.setRegistrationNumber(request.vehicleRegistration.registrationNumber);
        registration.setIssueDate(request.vehicleRegistration.issueDate);
        registration.setExpiryDate(request.vehicleRegistration.expiryDate);
        registration.setVehicle(vehicle); // Link registration to vehicle
        vehicle.setRegistration(registration); // Link vehicle to registration

        person.getVehicles().add(vehicle); // Add the fully constructed vehicle to the person's set

        // --- MAP AND LINK LICENSE (only if the person is new) ---
        if (person.getId() == null) {
            DriversLicense license = new DriversLicense();
            license.setLicenseNumber(request.driversLicense.licenseNumber);
            license.setIssueDate(request.driversLicense.issueDate);
            license.setExpiryDate(request.driversLicense.expiryDate);
            license.setCategories(request.driversLicense.categories);
            license.setPerson(person); // Link license to person
            person.setDriversLicense(license); // Link person to license
        }

        // --- SAVE ---
        // Saving the 'person' object will cascade and save all linked new entities.
        return personRepository.save(person);
    }
}