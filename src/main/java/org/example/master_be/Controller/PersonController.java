package org.example.master_be.Controller;

import org.example.master_be.DTO.PersonRequest;
import org.example.master_be.DTO.PersonResponse;
import org.example.master_be.Model.Person;
import org.example.master_be.Service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody PersonRequest request) {

        Person person = personService.createPerson(request);

        PersonResponse response = new PersonResponse(
                person.getId(),
                person.getUser().getId(), // 🔥 KLUCZ
                person.getGender(),
                person.getAge(),
                person.getWeight(),
                person.getHeight(),
                person.getActivityLevel(),
                person.getSleepHours(),
                person.getWaistCircumference(),
                person.getHipsCircumference(),
                person.getThighCircumference(),
                person.getBicepsCircumference(),
                person.getChestCircumference()
        );

        return ResponseEntity.ok(response);
    }
}