# Vehicle and Driver Registration System

This project is a Spring Boot application that provides a RESTful API for managing vehicle and driver registration records. It allows for creating, updating, deleting, and verifying registration data in a secure and structured manner.

## Technologies Used
*   **Java 17**
*   **Spring Boot 3**
*   **Spring Data JPA / Hibernate**: For database interaction.
*   **PostgreSQL**: As the relational database.
*   **Maven**: For project build and dependency management.
*   **Swagger / OpenAPI 3**: For API documentation and interactive testing.

## Getting Started

Follow these steps to get the project running on your local machine.

### 1. Prerequisites
*   **JDK 17** or later installed.
*   **Apache Maven** installed.
*   **PostgreSQL** database server running.

### 2. Database Setup
1.  Open `psql` or a tool like pgAdmin.
2.  Create a new database named `demo`.
    ```sql
    CREATE DATABASE demo;
    ```

### 3. Configuration
1.  Open the `src/main/resources/application.properties` file.
2.  Configure the database connection details:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/demo
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    ```
3.  Set the **admin password** for privileged operations (update/delete). This is a secret password that only an authority should know.
    ```properties
    # Password for privileged update/delete operations
    app.admin.password=YouShoNoPass091
    ```
4.  The property `spring.jpa.hibernate.ddl-auto=update` will automatically create the necessary tables in your database the first time you run the application.

### 4. Running the Application
1.  Open a terminal in the root directory of the project.
2.  Run the application using the Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  The application will start on `http://localhost:8080`.

---

## How to Use Swagger UI

Swagger UI provides an interactive web interface to explore and test the API endpoints directly from your browser.

1.  **Access Swagger UI**: Once the application is running, navigate to:
    [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

2.  **Testing an Endpoint (Example: Create Record)**:
    *   Find the `registration-controller` section and expand the `POST /api/register/createRecord` endpoint.
    *   Click the **"Try it out"** button on the right.
    *   The "Request body" field will become editable. Paste a valid JSON payload into it (see samples below).
    *   Click the **"Execute"** button.
    *   Scroll down to the "Responses" section to see the HTTP status code and the JSON response from the server.

---

## REST API Endpoints

All responses are wrapped in a `ServerResponse` JSON object: `{"status": boolean, "message": "string"}`.

### Registration API (`/api/register`)

#### 1. Create a New Registration
*   **Endpoint**: `POST /api/register/createRecord`
*   **Description**: Registers a new vehicle for a person. If the person (identified by `nationalId`) does not exist, they will be created. If the person exists but the vehicle does not, the new vehicle will be added to their record.
*   **Request Body**:
    ```json
    {
      "vehicleRegistration": {
        "registrationNumber": "34ABC123", "issueDate": "2023-05-15", "expiryDate": "2028-05-15",
        "owner": { "nationalId": "12345678901" },
        "vehicle": { "make": "Toyota", "model": "Corolla", "year": 2021, "color": "White", "vin": "VIN_TOYOTA_123", "engineNumber": "ENG_TOYOTA_123", "plateNumber": "34ABC123", "fuelType": "Gasoline" }
      },
      "driversLicense": {
        "licenseNumber": "TR12345678", "issueDate": "2022-03-10", "expiryDate": "2032-03-10", "categories": ["B", "A2"],
        "holder": { "firstName": "John", "lastName": "Doe", "dateOfBirth": "1985-07-20", "nationalId": "12345678901" }
      }
    }
    ```
*   **Responses**:
    *   **201 Created (Success)**: `{"status": true, "message": "Registration successful"}`
    *   **400 Bad Request**: `{"status": false, "message": "Invalid JSON format in request body."}` or `{"status": false, "message": "The registration request is incomplete."}`

#### 2. Delete a Vehicle
*   **Endpoint**: `DELETE /api/register/vehicleDelete`
*   **Description**: Deletes a single vehicle from the database, identified by its unique system-generated `id`. Requires authorization.
*   **Request Body**:
    ```json
    {
      "vehicleId": 1,
      "adminPassword": "YouShoNoPass091"
    }
    ```
*   **Responses**:
    *   **200 OK (Success)**: `{"status": true, "message": "Vehicle with ID 1 was successfully deleted."}`
    *   **403 Forbidden**: `{"status": false, "message": "Invalid admin password. Deletion not permitted."}`
    *   **404 Not Found**: `{"status": false, "message": "No vehicle found with the provided ID: 1"}`

### Verification API (`/api/verify`)

#### 1. Verify a Full Registration
*   **Endpoint**: `POST /api/verify/full`
*   **Description**: Performs a strict, field-by-field comparison of the provided data against the record in the database.
*   **Request Body**: The JSON should be identical to the data stored in the database.
*   **Responses**:
    *   **200 OK (Success - Verified)**: `{"status": true, "message": "Verification successful"}`
    *   **200 OK (Success - Not Verified)**: `{"status": false, "message": "Verification failed: Data does not match."}`
    *   **404 Not Found**: `{"status": false, "message": "Verification failed: No person found with national ID ..."}`

---

### **CRITICAL BUG WARNING**

There is a critical security bug in the `deleteVehicle` method in `RegistrationService.java`. The password check logic is reversed.

**Incorrect Code in your file:**
```java
if (request.adminPassword == null || request.adminPassword.equals(configuredAdminPassword)) {
    response.setMessage("Invalid admin password. Deletion not permitted.");
    return response;
}
```
This code **rejects the correct password** and allows any incorrect password.

**Corrected Code:**
You must change `.equals` to `!request.adminPassword.equals`.
```java
if (request.adminPassword == null || !request.adminPassword.equals(configuredAdminPassword)) {
    response.setMessage("Invalid admin password. Deletion not permitted.");
    return response;
}
```
Please apply this fix to secure your delete endpoint.
```# Vehicle and Driver Registration System

This project is a Spring Boot application that provides a RESTful API for managing vehicle and driver registration records. It allows for creating, updating, deleting, and verifying registration data in a secure and structured manner.

## Technologies Used
*   **Java 17**
*   **Spring Boot 3**
*   **Spring Data JPA / Hibernate**: For database interaction.
*   **PostgreSQL**: As the relational database.
*   **Maven**: For project build and dependency management.
*   **Swagger / OpenAPI 3**: For API documentation and interactive testing.

## Getting Started

Follow these steps to get the project running on your local machine.

### 1. Prerequisites
*   **JDK 17** or later installed.
*   **Apache Maven** installed.
*   **PostgreSQL** database server running.

### 2. Database Setup
1.  Open `psql` or a tool like pgAdmin.
2.  Create a new database named `demo`.
    ```sql
    CREATE DATABASE demo;
    ```

### 3. Configuration
1.  Open the `src/main/resources/application.properties` file.
2.  Configure the database connection details:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/demo
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    ```
3.  Set the **admin password** for privileged operations (update/delete). This is a secret password that only an authority should know.
    ```properties
    # Password for privileged update/delete operations
    app.admin.password=YouShoNoPass091
    ```
4.  The property `spring.jpa.hibernate.ddl-auto=update` will automatically create the necessary tables in your database the first time you run the application.

### 4. Running the Application
1.  Open a terminal in the root directory of the project.
2.  Run the application using the Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  The application will start on `http://localhost:8080`.

---

## How to Use Swagger UI

Swagger UI provides an interactive web interface to explore and test the API endpoints directly from your browser.

1.  **Access Swagger UI**: Once the application is running, navigate to:
    [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

2.  **Testing an Endpoint (Example: Create Record)**:
    *   Find the `registration-controller` section and expand the `POST /api/register/createRecord` endpoint.
    *   Click the **"Try it out"** button on the right.
    *   The "Request body" field will become editable. Paste a valid JSON payload into it (see samples below).
    *   Click the **"Execute"** button.
    *   Scroll down to the "Responses" section to see the HTTP status code and the JSON response from the server.

---

## REST API Endpoints

All responses are wrapped in a `ServerResponse` JSON object: `{"status": boolean, "message": "string"}`.

### Registration API (`/api/register`)

#### 1. Create a New Registration
*   **Endpoint**: `POST /api/register/createRecord`
*   **Description**: Registers a new vehicle for a person. If the person (identified by `nationalId`) does not exist, they will be created. If the person exists but the vehicle does not, the new vehicle will be added to their record.
*   **Request Body**:
    ```json
    {
      "vehicleRegistration": {
        "registrationNumber": "34ABC123", "issueDate": "2023-05-15", "expiryDate": "2028-05-15",
        "owner": { "nationalId": "12345678901" },
        "vehicle": { "make": "Toyota", "model": "Corolla", "year": 2021, "color": "White", "vin": "VIN_TOYOTA_123", "engineNumber": "ENG_TOYOTA_123", "plateNumber": "34ABC123", "fuelType": "Gasoline" }
      },
      "driversLicense": {
        "licenseNumber": "TR12345678", "issueDate": "2022-03-10", "expiryDate": "2032-03-10", "categories": ["B", "A2"],
        "holder": { "firstName": "John", "lastName": "Doe", "dateOfBirth": "1985-07-20", "nationalId": "12345678901" }
      }
    }
    ```
*   **Responses**:
    *   **201 Created (Success)**: `{"status": true, "message": "Registration successful"}`
    *   **400 Bad Request**: `{"status": false, "message": "Invalid JSON format in request body."}` or `{"status": false, "message": "The registration request is incomplete."}`

#### 3. Delete a Vehicle
*   **Endpoint**: `DELETE /api/register/vehicleDelete`
*   **Description**: Deletes a single vehicle from the database, identified by its unique system-generated `id`. Requires authorization.
*   **Request Body**:
    ```json
    {
      "vehicleId": 1,
      "adminPassword": "YouShoNoPass091"
    }
    ```
*   **Responses**:
    *   **200 OK (Success)**: `{"status": true, "message": "Vehicle with ID 1 was successfully deleted."}`
    *   **403 Forbidden**: `{"status": false, "message": "Invalid admin password. Deletion not permitted."}`
    *   **404 Not Found**: `{"status": false, "message": "No vehicle found with the provided ID: 1"}`

### Verification API (`/api/verify`)

#### 1. Verify a Full Registration
*   **Endpoint**: `POST /api/verify/full`
*   **Description**: Performs a strict, field-by-field comparison of the provided data against the record in the database.
*   **Request Body**: The JSON should be identical to the data stored in the database.
*   **Responses**:
    *   **200 OK (Success - Verified)**: `{"status": true, "message": "Verification successful"}`
    *   **200 OK (Success - Not Verified)**: `{"status": false, "message": "Verification failed: Data does not match."}`
    *   **404 Not Found**: `{"status": false, "message": "Verification failed: No person found with national ID ..."}`

---

### **CRITICAL BUG WARNING**

There is a critical security bug in the `deleteVehicle` method in `RegistrationService.java`. The password check logic is reversed.

**Incorrect Code in your file:**
```java
if (request.adminPassword == null || request.adminPassword.equals(configuredAdminPassword)) {
    response.setMessage("Invalid admin password. Deletion not permitted.");
    return response;
}
```
This code **rejects the correct password** and allows any incorrect password.

**Corrected Code:**
You must change `.equals` to `!request.adminPassword.equals`.
```java
if (request.adminPassword == null || !request.adminPassword.equals(configuredAdminPassword)) {
    response.setMessage("Invalid admin password. Deletion not permitted.");
    return response;
}
```
Please apply this fix to secure your delete
