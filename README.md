# Vehicle and Driver Registration & Verification API

A Spring Boot service for registering and strictly verifying driver and vehicle data. Uses PostgreSQL for persistence and Swagger UI for interactive API docs.

---

## Contents
- Overview
- Prerequisites
- Database Configuration
- Run the Application
- API Documentation (Swagger UI)
- Using the Terminal (PowerShell)
  - Register data
  - Verify data
- Response Model (ServerResponse)
- API Endpoints Reference
- Project Structure
- Troubleshooting

---

## Overview
- Purpose: Persist a person, their driver’s license, a vehicle, and its registration; verify later with a strict field-by-field comparison.
- Data model: Person ↔ DriversLicense, Person ↔ Vehicles, Vehicle ↔ VehicleRegistration.
- Responses: All controller responses are JSON objects using ServerResponse (status, message).

---

## Prerequisites
- Java JDK 21
- Maven
- PostgreSQL (local instance)
- Git (optional)

---

## Database Configuration
Edit src/main/resources/application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/demo
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Create the database:

```sql
CREATE DATABASE demo;
```

---

## Run the Application
From the project root:

```bash
# Windows PowerShell
cd C:\Users\a\Desktop\demo
./mvnw spring-boot:run
```

Wait until you see “Started AyrotekApplication ...”.  
Service URL:
- http://localhost:8080

---

## API Documentation (Swagger UI)
Open the interactive API docs:

- http://localhost:8080/swagger-ui.html

How to use:
1. Expand an endpoint (e.g., POST /api/register/full).
2. Click Try it out.
3. Paste a full JSON request body.
4. Click Execute and review the Server response section.

---

## Using the Terminal (PowerShell)

### Register data
Define and send a registration payload:

```powershell
# Define registration data
$jsonData = @{
  vehicleRegistration = @{
    registrationNumber = "34ABC123"
    issueDate = "2023-05-15"
    expiryDate = "2028-05-15"
    owner = @{ nationalId = "12345678901" }
    vehicle = @{
      make = "Toyota"; model = "Corolla"; year = 2021; color = "White"
      vin = "1HGCM82633A004352"; engineNumber = "ENG123456789"
      plateNumber = "34ABC123"; fuelType = "Gasoline"
    }
  }
  driversLicense = @{
    licenseNumber = "TR12345678"
    issueDate = "2022-03-10"
    expiryDate = "2032-03-10"
    categories = @("B","A2")
    holder = @{
      firstName = "John"; lastName = "Doe"; dateOfBirth = "1985-07-20"
      nationalId = "12345678901"
    }
  }
} | ConvertTo-Json -Depth 6

# Send registration request
Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/register/full" `
  -ContentType "application/json" -Body $jsonData
```

Expected JSON responses:
- 201 Created
  ```json
  { "status": true, "message": "Registration successful" }
  ```
- 409 Conflict (duplicate)
  ```json
  { "status": false, "message": "Record already exists" }
  ```
- 400 Bad Request
  ```json
  { "status": false, "message": "Validation error message" }
  ```

### Verify data
Use the same structure (every field must match exactly):

```powershell
Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/verify/full" `
  -ContentType "application/json" -Body $jsonData
```

Expected JSON responses:
- 200 OK
  ```json
  { "status": true, "message": "Verification successful" }
  ```
- 401 Unauthorized
  ```json
  { "status": false, "message": "Verification failed" }
  ```

Tips:
- The person is identified by driversLicense.holder.nationalId.
- Any mismatch in any field results in “Verification failed”.

---

## Response Model (ServerResponse)
All endpoints return a JSON wrapper defined by ServerResponse:

```json
{
  "status": boolean,
  "message": "string"
}
```

- status: true (success) or false (error).
- message: human-readable description (e.g., “Registration successful”, “Record already exists”, “Verification failed”).

---

## API Endpoints Reference

### POST /api/register/full
- Description: Registers a person, driver’s license, vehicle, and vehicle registration in one transaction.
- Request body: Full JSON as shown above.
- Responses:
  - 201 Created: `{ "status": true, "message": "Registration successful" }`
  - 409 Conflict: `{ "status": false, "message": "Record already exists" }`
  - 400 Bad Request: `{ "status": false, "message": "<details>" }`

### POST /api/verify/full
- Description: Strict, field-by-field verification against stored data.
- Request body: Same structure as registration.
- Responses:
  - 200 OK: `{ "status": true, "message": "Verification successful" }`
  - 401 Unauthorized: `{ "status": false, "message": "Verification failed" }`

Reason for POST-only:
- Payloads are large and nested.
- Sensitive identifiers should not be placed in URLs.
- Avoid URL length limits and log exposure.

---

## Project Structure

```
src/
├── main/java/Ayrotek/demo/
│   ├── AyrotekApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── RegistrationController.java
│   │   ├── VerificationController.java
│   │   └── ServerResponse.java
│   ├── dto/
│   │   └── RegistrationRequest.java
│   ├── entity/
│   │   ├── Person.java
│   │   ├── DriversLicense.java
│   │   ├── Vehicle.java
│   │   └── VehicleRegistration.java
│   ├── repository/
│   │   ├── PersonRepository.java
│   │   └── VerRepository.java
│   └── service/
│       ├── RegistrationService.java
│       └── VerificationService.java
└── resources/
    ├── application.properties
    ├── static/
    └── templates/
```

---

## Troubleshooting
- Cannot connect: Ensure the app is running (`./mvnw spring-boot:run`) and port 8080 is free.
- 409 on registration: Duplicate vehicle for the same person.
- 401 on verification: One or more fields differ from the stored record.
- Clean test data: Drop or delete rows in dependent tables first (vehicle_registrations, vehicles, drivers_licenses) before persons.