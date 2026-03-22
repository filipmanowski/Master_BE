package org.example.master_be.Repository;

import org.example.master_be.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUserId(Long userId);
}