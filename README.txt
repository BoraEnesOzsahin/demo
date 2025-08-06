==================================================
 Ayrotek Verification Microservice - README
==================================================

1. Project Overview
-------------------
This is a simple Spring Boot microservice designed to verify user information against a database. It exposes RESTful API endpoints to check if a given combination of ID, plate number, and serial number exists and is valid. The service is secured using Spring Security with Basic Authentication.

2. Technologies Used
--------------------
- Java 21
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Spring Security
- PostgreSQL
- Maven
- Lombok

3. Database Structure
---------------------
The application uses a single PostgreSQL table named `userinfo` to store the verification data.

Table: `userinfo`
- This table holds the records that the service will check against.

Columns:
- `id` (VARCHAR, Primary Key): The user's unique identifier (e.g., a national ID, 11 characters).
- `platenumber` (VARCHAR): The vehicle's license plate number.
- `serial_num` (VARCHAR): A unique serial number associated with the record.
- `is_verified` (BOOLEAN): A flag that is updated to `true` upon successful verification.

Sample SQL to create the table:
```sql
CREATE TABLE userinfo (
    id VARCHAR(255) PRIMARY KEY,
    platenumber VARCHAR(255) NOT NULL,
    serial_num VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE
);
```

Sample data insertion:
```sql
INSERT INTO userinfo (id, platenumber, serial_num) VALUES ('12345678901', '34ABC123', 'X0FD53');
```

4. How to Run the Project
-------------------------
**Prerequisites:**
- JDK 21 or later
- Apache Maven
- A running PostgreSQL instance

**Steps:**
1.  **Database Setup:**
    - Create a PostgreSQL database named `demo`.
    - Ensure you have a user (e.g., `postgres`) with privileges to access this database.

2.  **Configure Application:**
    - Open the `src/main/resources/application.properties` file.
    - Update the datasource properties to match your PostgreSQL setup:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/demo
      spring.datasource.username=your_postgres_username
      spring.datasource.password=your_postgres_password
      ```

3.  **Build and Run:**
    - Open a terminal or command prompt in the project's root directory (`c:\Users\a\Desktop\demo`).
    - Run the following Maven command:
      ```bash
      mvn spring-boot:run
      ```
    - The application will start and be accessible on `http://localhost:8080`.

5. RESTful API Endpoints
------------------------
The service exposes two main endpoints under the base path `/api/verify`.

**A. POST /api/verify/DataVerification (Primary Endpoint)**
   - This is the main endpoint for verifying data. It requires authentication.

   - **Method:** `POST`
   - **Authentication:** Basic Authentication.
     - Username: `admin`
     - Password: `admin123`
     (Credentials are configured in `SecurityConfig.java`)
   - **Request Body (JSON):**
     ```json
     {
         "id": "12345678901",
         "plateNumber": "34ABC123",
         "serialNum": "X0FD53"
     }
     ```
   - **Success Response (200 OK):**
     ```
     Registration verified successfully
     ```
   - **Failure Response (401 Unauthorized):**
     ```
     Registration verification failed
     ```

**B. GET /api/verify/DataVerification/get (Testing Endpoint)**
   - This endpoint was added for easy testing directly from a web browser, without needing a tool like Postman or cURL. It does not require authentication.

   - **Method:** `GET`
   - **Authentication:** None (`permitAll` is configured in `SecurityConfig.java`).
   - **Usage:** Provide the data as URL query parameters.
   - **Example URL:**
     ```
     http://localhost:8080/api/verify/DataVerification/get?plateNumber=34ABC123&id=12345678901&serialNum=X0FD53
     ```
   - **How to Test:**
     1. Start the application.
     2. Open a web browser.
     3. Paste the example URL into the address bar.
     4. Change the values for `plateNumber`, `id`, and `serialNum` to your test data.
     5. Press Enter. The browser will display the verification result.
```// filepath: c:\Users\a\Desktop\demo\README.txt
==================================================
 Ayrotek Verification Microservice - README
==================================================

1. Project Overview
-------------------
This is a simple Spring Boot microservice designed to verify user information against a database. It exposes RESTful API endpoints to check if a given combination of ID, plate number, and serial number exists and is valid. The service is secured using Spring Security with Basic Authentication.

2. Technologies Used
--------------------
- Java 21
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Spring Security
- PostgreSQL
- Maven
- Lombok

3. Database Structure
---------------------
The application uses a single PostgreSQL table named `userinfo` to store the verification data.

Table: `userinfo`
- This table holds the records that the service will check against.

Columns:
- `id` (VARCHAR, Primary Key): The user's unique identifier (e.g., a national ID, 11 characters).
- `platenumber` (VARCHAR): The vehicle's license plate number.
- `serial_num` (VARCHAR): A unique serial number associated with the record.
- `is_verified` (BOOLEAN): A flag that is updated to `true` upon successful verification.

Sample SQL to create the table:
```sql
CREATE TABLE userinfo (
    id VARCHAR(255) PRIMARY KEY,
    platenumber VARCHAR(255) NOT NULL,
    serial_num VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE
);
```

Sample data insertion:
```sql
INSERT INTO userinfo (id, platenumber, serial_num) VALUES ('12345678901', '34ABC123', 'X0FD53');
```

4. How to Run the Project
-------------------------
**Prerequisites:**
- JDK 21 or later
- Apache Maven
- A running PostgreSQL instance

**Steps:**
1.  **Database Setup:**
    - Create a PostgreSQL database named `demo`.
    - Ensure you have a user (e.g., `postgres`) with privileges to access this database.

2.  **Configure Application:**
    - Open the `src/main/resources/application.properties` file.
    - Update the datasource properties to match your PostgreSQL setup:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/demo
      spring.datasource.username=your_postgres_username
      spring.datasource.password=your_postgres_password
      ```

3.  **Build and Run:**
    - Open a terminal or command prompt in the project's root directory (`c:\Users\a\Desktop\demo`).
    - Run the following Maven command:
      ```bash
      mvn spring-boot:run
      ```
    - The application will start and be accessible on `http://localhost:8080`.

5. RESTful API Endpoints
------------------------
The service exposes two main endpoints under the base path `/api/verify`.

**A. POST /api/verify/DataVerification (Primary Endpoint)**
   - This is the main endpoint for verifying data. It requires authentication.

   - **Method:** `POST`
   - **Authentication:** Basic Authentication.
     - Username: `admin`
     - Password: `admin123`
     (Credentials are configured in `SecurityConfig.java`)
   - **Request Body (JSON):**
     ```json
     {
         "id": "12345678901",
         "plateNumber": "34ABC123",
         "serialNum": "X0FD53"
     }
     ```
   - **Success Response (200 OK):**
     ```
     Registration verified successfully
     ```
   - **Failure Response (401 Unauthorized):**
     ```
     Registration verification failed
     ```

**B. GET /api/verify/DataVerification/get (Testing Endpoint)**
   - This endpoint was added for easy testing directly from a web browser, without needing a tool like Postman or cURL. It does not require authentication.

   - **Method:** `GET`
   - **Authentication:** None (`permitAll` is configured in `SecurityConfig.java`).
   - **Usage:** Provide the data as URL query parameters.
   - **Example URL:**
     ```
     http://localhost:8080/api/verify/DataVerification/get?plateNumber=34ABC123&id=12345678901&serialNum=X0FD53
     ```
   - **How to Test:**
     1. Start the application.
     2. Open a web browser.
     3. Paste the example URL into the address bar.
     4. Change the values for `plateNumber`, `id`, and `serialNum` to your test data.
     5. Press Enter. The browser will