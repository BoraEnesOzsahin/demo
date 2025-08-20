package Ayrotek.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Data
@EqualsAndHashCode(exclude = {"owner", "registration", "vehicle_type"}) // Exclude relationships
@ToString(exclude = {"owner", "registration"}) // Exclude from toString too
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vin; // Vehicle Identification Number is a good primary key candidate


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private VehicleType vehicleType;

    private String company; //Only for commercials

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


    @JsonIgnoreProperties("vehicle")
    public VehicleRegistration getRegistration() {
        return registration;
    }
}