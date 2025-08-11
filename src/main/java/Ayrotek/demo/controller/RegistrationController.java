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



    // POST /api/register/full -> accepts your full JSON and persists Person + Vehicle + License
    @PostMapping(value = "/full", consumes = "application/json", produces = "application/json")
    public ServerResponse registerFull(@RequestBody RegistrationRequest request) {
        ServerResponse response = new ServerResponse();

        try {
            
            Person saved = registrationService.processRegistration(request);
            response.setMessage("Registration successful");
            response.setStatus(true);

            return response;

        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return response;
            
        } catch (Exception e) {
            response.setMessage("Registration failed");
            response.setStatus(false);
            return response;
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ServerResponse handleInvalidJson(HttpMessageNotReadableException e) {
        ServerResponse response = new ServerResponse();
        response.setMessage("Invalid JSON: " + e.getMessage());
        response.setStatus(false);
        return response;
    }
}