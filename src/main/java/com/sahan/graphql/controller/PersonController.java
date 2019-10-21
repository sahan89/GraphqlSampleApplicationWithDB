package com.sahan.graphql.controller;

import com.sahan.graphql.model.Person;
import com.sahan.graphql.repository.PersonRepository;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/personController")
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @Value("classpath:person.graphql")
    private Resource schemaResource;

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        DataFetcher<List<Person>> dataFetcher1 = data -> {
            List<Person> allPersonsList = personRepository.findAll();
            return allPersonsList;
        };
        DataFetcher<List<Person>> dataFetcher2 = data -> {
            List<Person> personsList = personRepository.findPersonByFirstName(data.getArgument("firstName"));
            return personsList;
        };
        return RuntimeWiring.newRuntimeWiring().type("Query", typeWriting ->
                typeWriting.dataFetcher("getAllPersons", dataFetcher1).dataFetcher("findPersonByFirstName", dataFetcher2)).build();
    }

    @PostMapping("/addPerson")
    public String addPerson(@RequestBody Person person) {
        Person savedPerson = personRepository.save(person);
        return "Record Inserted : " + savedPerson.getId() + " " + savedPerson.getFirstName() + " " + savedPerson.getLastName();
    }

    @GetMapping("/getAllPersons")
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/findPersonByFirstName")
    public ResponseEntity<Object> findPersonByFirstName(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
