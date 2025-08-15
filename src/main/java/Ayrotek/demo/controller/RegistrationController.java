package Ayrotek.demo.controller;

import Ayrotek.demo.dto.DeleteVehicleRequest; // Import the new DTO
import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.entity.Person;
import Ayrotek.demo.service.RegistrationService;
import jakarta.persistence.EntityNotFoundException; // Make sure you have this exception class
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * POST endpoint for CREATING a new registration.
     * On success, returns a ServerResponse with status 201 (Created).
     */
    @PostMapping(value = "/createRecord")
    public ResponseEntity<ServerResponse> createRegistration(@RequestBody RegistrationRequest request) {
        // This method now only handles the success case.
        registrationService.processRegistration(request);

        ServerResponse response = new ServerResponse();
        response.setStatus(true);
        response.setMessage("Registration successful");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT endpoint for UPDATING an existing registration using your workflow.
     * On success, returns a ServerResponse with status 200 (OK).
     */
    /* 
    @PutMapping(value = "/updateRecord")
    public ResponseEntity<ServerResponse> updateRegistrationRecord(@RequestBody RegistrationRequest request) {
        // This method also only handles the success case.
        registrationService.updateRegistration(request);

        ServerResponse response = new ServerResponse();
        response.setStatus(true);
        response.setMessage("Update successful");

        return ResponseEntity.ok(response);
    }*/

    /**
     * DELETE endpoint for removing a single vehicle by its ID.
     */
    @DeleteMapping("/vehicleDelete")
    public ResponseEntity<ServerResponse> deleteVehicle(@RequestBody DeleteVehicleRequest request) {
        ServerResponse response = registrationService.deleteVehicle(request);

        // If the operation was successful, return 200 OK.
        // If it failed (e.g., wrong password), the service returns a response with status=false,
        // and we can return a 400 Bad Request or 403 Forbidden depending on the message.
        if (response.isStatus()) {
            return ResponseEntity.ok(response);
        } else {
            // You can add more specific logic here based on the error message if needed
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- Centralized Exception Handling ---

    /**
     * Handles errors related to invalid authorization (e.g., wrong admin password).
     * Returns a ServerResponse with status 403 (Forbidden).
     */
    /*@ExceptionHandler(AdminAuthorizationException.class)
    public ResponseEntity<ServerResponse> handleAdminAuth(AdminAuthorizationException ex) {
        ServerResponse response = new ServerResponse();
        response.setStatus(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    } */

    /**
     * Handles errors when a record (like a Person or Vehicle) cannot be found.
     * Returns a ServerResponse with status 404 (Not Found).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ServerResponse> handleNotFound(EntityNotFoundException ex) {
        ServerResponse response = new ServerResponse();
        response.setStatus(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles validation errors and other bad requests from the client.
     * Returns a ServerResponse with status 400 (Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ServerResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ServerResponse response = new ServerResponse();
        response.setStatus(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles errors caused by malformed JSON in the request body.
     * Returns a ServerResponse with status 400 (Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ServerResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        ServerResponse response = new ServerResponse();
        response.setStatus(false);
        response.setMessage("Invalid JSON format in request body.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}