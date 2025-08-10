package Ayrotek.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Data
@EqualsAndHashCode(exclude = {"owner", "registration"}) // Exclude relationships
@ToString(exclude = {"owner", "registration"}) // Exclude from toString too
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vin; // Vehicle Identification Number is a good primary key candidate

    private String plateNumber;
    private String make;
    private String model;
    private int year;
    private String color;
    private String engineNumber;
    private String fuelType;

    // This defines the foreign key link to the Person (owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person owner;

    // A vehicle has one registration document
    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private VehicleRegistration registration;
}