package Ayrotek.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "persons")
@Data
@EqualsAndHashCode(exclude = {"vehicles", "driversLicense"}) // Exclude relationships
@ToString(exclude = {"vehicles", "driversLicense"}) // Exclude from toString too
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nationalId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String regCode; // Registration code, unique for each person

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private DriversLicense driversLicense;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles = new HashSet<>();


    
    @JsonIgnoreProperties("owner")
    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    
    @JsonIgnoreProperties("person")
    public DriversLicense getDriversLicense() {
        return driversLicense;
    }
    
}