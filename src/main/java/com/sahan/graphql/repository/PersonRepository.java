package com.sahan.graphql.repository;

import com.sahan.graphql.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findPersonByFirstName(String firstName);
}
