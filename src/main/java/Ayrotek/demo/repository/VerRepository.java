package Ayrotek.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Ayrotek.demo.entity.Info;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerRepository extends JpaRepository<Info, String> {
    Optional<Info> findByPlateNumber(String plateNumber);
}


