package Ayrotek.demo.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "userinfo")
@Data

public class Info {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "serial_num")
    private String serialNum;

    @Column(name = "is_verified")
    private boolean isVerified;
    
}
