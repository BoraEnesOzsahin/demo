package Ayrotek.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_registrations")
@Data
@EqualsAndHashCode(exclude = {"vehicle"}) // Exclude relationship
@ToString(exclude = {"vehicle"}) // Exclude from toString too
public class VehicleRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @OneToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    private Vehicle vehicle;
}