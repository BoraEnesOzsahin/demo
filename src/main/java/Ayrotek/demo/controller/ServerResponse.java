package Ayrotek.demo.controller;

import Ayrotek.demo.dto.RegistrationRequest;
import Ayrotek.demo.entity.Person;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServerResponse {
    private boolean status;
    private String message;

    }