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
- Purpose: Persist a person, their driver’s license, a vehicle, and its registration. Verify later with a field-by-field, strict comparison.
- Data model: Person ↔ DriversLicense, Person ↔ Vehicles, Vehicle ↔ VehicleRegistration
- Responses: All controller responses return a JSON object (wrapped) rather than plain text.

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

Wait until you see “Started AyrotekApplication...” The app will run at:
- http://localhost:8080

---

## API Documentation (Swagger UI)
Open the interactive API docs in your browser:

- http://localhost:8080/swagger-ui.html

How to use:
1. Expand an endpoint (for example, POST /api/register/full).
2. Click Try it out.
3. Paste a full JSON request body.
4. Click Execute and view the server response.

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

Expected wrapped JSON responses:
- 201 Created:
  ```json
  {
    "status": true,
    "message": "Registration successful",
    "data": {
      "...": "Person object with nested relations"
    }
  }
  ```
- 409 Conflict (duplicate):
  ```json
  {
    "status": false,
    "message": "Record already exists",
    "data": null
    }
  ```
- 400 Bad Request:
  ```json
  {
    "status": false,
    "message": "Validation error message",
    "data": null
  }
  ```

### Verify data
Use the same structure (every field must match exactly):

```powershell
Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/verify/full" `
  -ContentType "application/json" -Body $jsonData
```

Expected wrapped JSON responses:
- 200 OK:
  ```json
  {
    "status": true,
    "message": "Verification successful",
    "data": null
  }
  ```
- 401 Unauthorized:
  ```json
  {
    "status": false,
    "message": "Verification failed",
    "data": null
  }
  ```

Tip:
- The person is identified by driversLicense.holder.nationalId.
- A single mismatch in any field results in “Verification failed”.

---

## Response Model (ServerResponse)
All endpoints return a JSON wrapper object (as defined in ServerResponse.java):

```json
{
  "status": boolean,
  "message": "string",
  "data": any | null
}
```

- status: true for success, false for errors.
- message: human-readable description (e.g., “Registration successful”, “Record already exists”).
- data: optional payload (e.g., on successful registration, the persisted Person object). For verification, data is typically null.

Note: If your current controller returns raw entities on success, align it to wrap with ServerResponse for consistency.

---

## API Endpoints Reference

### POST /api/register/full
- Description: Registers a person, driver’s license, vehicle, and vehicle registration in one transaction.
- Request body: Full JSON as shown above.
- Responses:
  - 201 Created: ServerResponse with data = Person object JSON.
  - 409 Conflict: ServerResponse with message = “Record already exists”.
  - 400 Bad Request: ServerResponse with validation/processing error message.

### POST /api/verify/full
- Description: Strict, field-by-field verification against stored data.
- Request body: Same structure as registration.
- Responses:
  - 200 OK: ServerResponse { status: true, message: “Verification successful”, data: null }.
  - 401 Unauthorized: ServerResponse { status: false, message: “Verification failed”, data: null }.