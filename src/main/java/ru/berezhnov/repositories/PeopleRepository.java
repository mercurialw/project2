package ru.berezhnov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezhnov.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Person findByFullName(String name);
}
