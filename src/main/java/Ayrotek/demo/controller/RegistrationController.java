package Ayrotek.demo.controller;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.entity.Person;
import Ayrotek.demo.service.RegistrationService;
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

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("register-ok");
    }

    // POST /api/register/full -> accepts your full JSON and persists Person + Vehicle + License
    @PostMapping(value = "/full", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerFull(@RequestBody RegistrationRequest request) {
        try {
            Person saved = registrationService.processRegistration(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed");
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidJson(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("Invalid JSON: " + e.getMessage());
    }
}