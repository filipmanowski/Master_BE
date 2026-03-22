package org.example.master_be.Service;

import org.example.master_be.DTO.PersonRequest;
import org.example.master_be.Model.Person;
import org.example.master_be.Model.User;
import org.example.master_be.Repository.PersonRepository;
import org.example.master_be.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    public PersonService(PersonRepository personRepository,
                         UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    public Person createPerson(PersonRequest request) {

        System.out.println("===== PERSON REQUEST DEBUG =====");
        System.out.println("Received: " + request);

        System.out.println("userId: " + request.getUserId());
        System.out.println("gender: " + request.getGender());
        System.out.println("age: " + request.getAge());
        System.out.println("weight: " + request.getWeight());
        System.out.println("height: " + request.getHeight());
        System.out.println("activityLevel: " + request.getActivityLevel());
        System.out.println("sleepHours: " + request.getSleepHours());
        System.out.println("waistCircumference: " + request.getWaistCircumference());
        System.out.println("hipsCircumference: " + request.getHipsCircumference());
        System.out.println("thighCircumference: " + request.getThighCircumference());
        System.out.println("bicepsCircumference: " + request.getBicepsCircumference());
        System.out.println("chestCircumference: " + request.getChestCircumference());

        System.out.println("================================");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // 🔒 zabezpieczenie: 1 user = 1 person
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

        Person saved = personRepository.save(person);

        user.setEnabled(true);
        userRepository.save(user);

        return saved;
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