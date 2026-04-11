package org.example.master_be.Service;

import org.example.master_be.DTO.PersonRequest;
import org.example.master_be.Model.Person;
import org.example.master_be.Model.User;
import org.example.master_be.Repository.PersonRepository;
import org.example.master_be.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    public PersonService(PersonRepository personRepository,
                         UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Person createPerson(PersonRequest request) {

        // 🔥 ZAMIAST ID → bierzemy usera po emailu
        // (na razie hardcode do testu — zmienimy później na auth)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 1 user = 1 person
        if (personRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Person already exists for this user");
        }

        Person person = new Person();
        person.setUser(user);
        person.setGender(mapGender(request.getGender()));
        person.setAge(request.getAge());
        person.setWeight(request.getWeight());
        person.setHeight(request.getHeight());
        person.setActivityLevel(request.getActivityLevel());
        person.setSleepHours(request.getSleepHours());
        person.setWaistCircumference(request.getWaistCircumference());
        person.setHipsCircumference(request.getHipsCircumference());
        person.setThighCircumference(request.getThighCircumference());
        person.setBicepsCircumference(request.getBicepsCircumference());
        person.setChestCircumference(request.getChestCircumference());

        personRepository.save(person);

        // ✅ Ustawiamy enabled = true na user'ze
        user.setEnabled(true);
        userRepository.save(user);

        return person;
    }

    private String mapGender(String gender) {
        if (gender == null) return null;

        return switch (gender.toLowerCase()) {
            case "mężczyzna", "mezczyzna", "male", "m" -> "MALE";
            case "kobieta", "female", "f" -> "FEMALE";
            default -> throw new RuntimeException("Invalid gender: " + gender);
        };
    }
}