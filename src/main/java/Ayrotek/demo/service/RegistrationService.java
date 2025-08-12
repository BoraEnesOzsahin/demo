package Ayrotek.demo.service;

import Ayrotek.demo.controller.ServerResponse;
import Ayrotek.demo.dto.DeleteVehicleRequest; // Import the new DTO
import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.dto.RegistrationRequest.*;
import Ayrotek.demo.entity.*;
import Ayrotek.demo.repository.PersonRepository;
import Ayrotek.demo.repository.VehicleRepository; // Import the new repository

import io.swagger.v3.oas.models.servers.Server;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository; // Add the vehicle repository

    @Value("${app.admin.password}")
    private String configuredAdminPassword;

    // Update the constructor to accept the new repository
    public RegistrationService(PersonRepository personRepository, VehicleRepository vehicleRepository) {
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
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

        // --- GENERATE REGISTRATION CODE ---
        String registrationCode = UUID.randomUUID().toString();
        person.setRegCode(registrationCode);

        // --- SAVE ---
        return personRepository.save(person);
    }

    public Object updateRegistration(RegistrationRequest request){
        ServerResponse response = new ServerResponse();

        if (request.regCode == null || request.regCode.isBlank()){
            response.setMessage("Registration code is required.");
            return response;
        }

        if(request.adminPassword == null || !request.adminPassword.equals(configuredAdminPassword)){
            response.setMessage("Invalid admin password. Update not permitted.");
            return response;
        }

        Person personToUpdate = personRepository.findByRegCode(request.regCode).orElse(null);

        if (personToUpdate != null) {
            // Update person details
            HolderDTO holderDto = request.driversLicense.holder;
            personToUpdate.setFirstName(holderDto.firstName);
            personToUpdate.setLastName(holderDto.lastName);
            personToUpdate.setDateOfBirth(holderDto.dateOfBirth);
            // National ID is the core identifier and should not be changed here.
            //personToUpdate.setNationalId(holderDto.nationalId);

            // Replace the Driver's License details
            DriversLicense licenseToUpdate = personToUpdate.getDriversLicense();
            licenseToUpdate.setLicenseNumber(request.driversLicense.licenseNumber);
            licenseToUpdate.setIssueDate(request.driversLicense.issueDate);
            licenseToUpdate.setExpiryDate(request.driversLicense.expiryDate);
            licenseToUpdate.setCategories(request.driversLicense.categories);

            // Find the specific vehicle to update using its plate number
            VehicleDTO vehicleDto = request.vehicleRegistration.vehicle;
            Optional<Vehicle> vehicleOptional = personToUpdate.getVehicles().stream()
                    .filter(v -> v.getPlateNumber().equals(vehicleDto.getPlateNumber()))
                    .findFirst();

            // Instead of throwing an exception, we check if the vehicle was found.
            if (vehicleOptional.isEmpty()) {
                // If not found, create and return your custom JSON response.
                response.setMessage("No vehicle with plate number " + vehicleDto.getPlateNumber() + " found for this person.");
                return response;
            }
            Vehicle vehicleToUpdate = vehicleOptional.get();
            // --- END OF CORRECTION ---


            // Replace the Vehicle and its Registration details
            vehicleToUpdate.setVin(vehicleDto.vin);
            vehicleToUpdate.setMake(vehicleDto.make);
            vehicleToUpdate.setModel(vehicleDto.model);
            vehicleToUpdate.setYear(vehicleDto.year);
            vehicleToUpdate.setColor(vehicleDto.color);
            vehicleToUpdate.setEngineNumber(vehicleDto.engineNumber);
            vehicleToUpdate.setFuelType(vehicleDto.fuelType);
            vehicleToUpdate.setPlateNumber(vehicleDto.plateNumber);

            VehicleRegistration registrationToUpdate = vehicleToUpdate.getRegistration();
            registrationToUpdate.setRegistrationNumber(request.vehicleRegistration.registrationNumber);
            registrationToUpdate.setIssueDate(request.vehicleRegistration.issueDate);
            registrationToUpdate.setExpiryDate(request.vehicleRegistration.expiryDate);

            // Generate a NEW regCode for this update transaction for traceability
            personToUpdate.setRegCode(UUID.randomUUID().toString());

            // Save the fully updated person record
            return personRepository.save(personToUpdate);
        } else {
            response.setMessage("Person not found with the provided registration code.");
            return response;
        }
    }

    /**
     * Deletes a single vehicle record, identified by its system-generated ID.
     * Requires admin password for authorization.
     */
    public ServerResponse deleteVehicle(DeleteVehicleRequest request) {
        ServerResponse response = new ServerResponse();

        // 1. Validate the request
        if (request.vehicleId == null) {
            response.setMessage("A vehicle ID is required to identify which vehicle to delete.");
            return response;
        }
        if (request.adminPassword == null || request.adminPassword.equals(configuredAdminPassword)) {
            response.setMessage("Invalid admin password. Deletion not permitted.");
            return response;
        }

        // 2. Check if the vehicle exists before trying to delete it
        boolean vehicleExists = vehicleRepository.existsById(request.vehicleId);
        if (!vehicleExists) {
            response.setMessage("No vehicle found with the provided ID: " + request.vehicleId);
            return response;
        }

        // 3. Delete the vehicle
        // The @OneToMany relationship in the Person entity has 'orphanRemoval = true',
        // so removing the vehicle from the database will also automatically remove it
        // from the person's set of vehicles.
        vehicleRepository.deleteById(request.vehicleId);

        // 4. Return a success response
        response.setStatus(true);
        response.setMessage("Vehicle with ID " + request.vehicleId + " was successfully deleted.");
        return response;
    }
}