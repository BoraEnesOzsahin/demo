package Ayrotek.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "drivers_licenses")
@Data
@EqualsAndHashCode(exclude = {"person"}) // Exclude relationship
@ToString(exclude = {"person"}) // Exclude from toString too
public class DriversLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licenseNumber;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "license_categories", joinColumns = @JoinColumn(name = "license_id"))
    @Column(name = "category")
    private List<String> categories;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @JsonIgnoreProperties("driversLicense")
    public Person getPerson() {
        return person;
    }

    
}
