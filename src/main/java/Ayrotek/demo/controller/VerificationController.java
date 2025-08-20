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
    public ServerResponse verifyFullRegistration(@RequestBody RegistrationRequest request) {
        ServerResponse response = new ServerResponse();

        try {
            String mismatchReason = verificationService.verifyAndGetMismatchReason(request);

            if (mismatchReason == null) {
                response.setMessage("Verification successful");
                response.setStatus(true);
            } else {
                response.setMessage("Verification failed: " + mismatchReason);
                response.setStatus(false);
            }
        } catch (Exception e) {
            // Catch any unexpected errors during the process
            response.setMessage("An error occurred during verification: " + e.getMessage());
            response.setStatus(false);
        }
        return response;
    }
}
