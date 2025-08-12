package Ayrotek.demo.repository;

import Ayrotek.demo.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByNationalId(String nationalId);
    Optional<Person> findByRegCode(String regCode);
}