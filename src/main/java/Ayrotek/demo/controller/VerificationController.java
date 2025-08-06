package Ayrotek.demo.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import Ayrotek.demo.service.VerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import Ayrotek.demo.entity.*;


@RestController
@RequestMapping("/api/verify")
public class VerificationController {
    
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
        // this.licenseVerificationService = licenseVerificationService;
    }


    @GetMapping("/DataVerification/get")
    public ResponseEntity<String> verifyInfoFromUrl(
            @RequestParam String plateNumber,
            @RequestParam String id,
            @RequestParam String serialNum) {

                 // Call the service layer with the parameters from the URL
        boolean isVerified = verificationService.verifyInfo(plateNumber, id, serialNum);

        if (isVerified) {
            return ResponseEntity.ok("Verification successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Verification failed");
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
            .body("Invalid JSON format: " + e.getMessage());
    }

    

    @PostMapping(value = "/DataVerification", consumes = "application/json")
    public ResponseEntity<String> verifyInfo(@RequestBody(required = true) Info request) {
        try {
            // Step 1: Validate JSON format
            if (request == null) {
                return ResponseEntity.badRequest()
                    .body("Invalid JSON format: Request body is empty");
            }

            // Step 2: Validate entity data
            if (!isValidInfoRequest(request)) {
                return ResponseEntity.badRequest()
                    .body("Invalid data format. Required: ID (11 digits), plateNumber, serialNum");
            }

            // Send to service layer and pdate the verification status in the database
            
            boolean isVerified = verificationService.verifyInfo(
                request.getPlateNumber(),
                request.getId(),
                request.getSerialNum()
            );

            

            // Notify user with result
            if (isVerified) {
                return ResponseEntity.ok()
                    .body("Registration verified successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Registration verification failed");
            }

        } catch (HttpMessageNotReadableException e) {
            // Step 1 error: Invalid JSON
            return ResponseEntity.badRequest()
                .body("Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during verification");
        }
    }

    // Helper methods for request validation
    private boolean isValidInfoRequest(Info request) {
        return request != null
            && request.getId() != null
            && request.getPlateNumber() != null
            && request.getSerialNum() != null
            && request.getId().length() == 11;
    }

  
}
