package Ayrotek.demo.controller;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Verifies a full registration record.
     * Expects a JSON body identical to the registration request.
     * It performs a strict, field-by-field comparison against the database.
     */
    @PostMapping(value = "/full", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> verifyFullRegistration(@RequestBody RegistrationRequest request) {
        try {
            boolean isVerified = verificationService.verifyRegistrationData(request);

            if (isVerified) {
                return ResponseEntity.ok("Verification successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verification failed");
            }
        } catch (Exception e) {
            // Catch any unexpected errors during the process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during verification: " + e.getMessage());
        }
    }
}
