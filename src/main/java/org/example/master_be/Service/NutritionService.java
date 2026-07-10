package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.MicronutrientRequest;
import org.example.master_be.DTO.MicronutrientResponse;
import org.example.master_be.DTO.NutritionEntryRequest;
import org.example.master_be.DTO.NutritionEntryResponse;
import org.example.master_be.DTO.NutritionSummaryResponse;
import org.example.master_be.Model.NutritionEntry;
import org.example.master_be.Model.NutritionMicronutrient;
import org.example.master_be.Model.User;
import org.example.master_be.Repository.NutritionEntryRepository;
import org.example.master_be.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionService {

    private final NutritionEntryRepository nutritionRepo;
    private final UserRepository userRepo;

    @Transactional
    public NutritionEntryResponse createEntry(Long userId, NutritionEntryRequest request) {
        validate(request);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        NutritionEntry entry = new NutritionEntry();
        entry.setUser(user);
        entry.setConsumedAt(request.getConsumedAt() == null ? LocalDateTime.now() : request.getConsumedAt());
        entry.setMealName(request.getMealName().trim());
        entry.setDescription(trimToNull(request.getDescription()));
        entry.setCalories(request.getCalories());
        entry.setProtein(request.getProtein());
        entry.setCarbohydrates(request.getCarbohydrates());
        entry.setFat(request.getFat());

        if (request.getMicronutrients() != null) {
            for (MicronutrientRequest microRequest : request.getMicronutrients()) {
                validateMicronutrient(microRequest);
                NutritionMicronutrient micronutrient = new NutritionMicronutrient();
                micronutrient.setNutritionEntry(entry);
                micronutrient.setName(microRequest.getName().trim());
                micronutrient.setAmount(microRequest.getAmount());
                micronutrient.setUnit(microRequest.getUnit().trim());
                entry.getMicronutrients().add(micronutrient);
            }
        }

        return mapToDto(nutritionRepo.save(entry));
    }

    @Transactional
    public void deleteEntry(Long entryId, Long userId) {

        NutritionEntry entry = nutritionRepo.findById(entryId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        System.out.println("========= DELETE =========");
        System.out.println("entryId = " + entryId);
        System.out.println("entry.user.id = " + entry.getUser().getId());
        System.out.println("jwt.user.id = " + userId);
        System.out.println("==========================");

        if (!entry.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        nutritionRepo.delete(entry);
    }

    @Transactional(readOnly = true)
    public List<NutritionEntryResponse> getEntries(Long userId) {
        return nutritionRepo.findByUserIdOrderByConsumedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public NutritionSummaryResponse getDailySummary(Long userId, LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        List<NutritionEntry> entries = nutritionRepo.findByUserIdAndConsumedAtBetweenOrderByConsumedAtAsc(
                userId,
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay()
        );

        NutritionSummaryResponse response = new NutritionSummaryResponse();
        response.setDate(targetDate);
        response.setCalories(entries.stream().mapToInt(NutritionEntry::getCalories).sum());
        response.setProtein(entries.stream().mapToDouble(NutritionEntry::getProtein).sum());
        response.setCarbohydrates(entries.stream().mapToDouble(NutritionEntry::getCarbohydrates).sum());
        response.setFat(entries.stream().mapToDouble(NutritionEntry::getFat).sum());
        return response;
    }

    private NutritionEntryResponse mapToDto(NutritionEntry entry) {
        NutritionEntryResponse dto = new NutritionEntryResponse();
        dto.setId(entry.getId());
        dto.setConsumedAt(entry.getConsumedAt());
        dto.setMealName(entry.getMealName());
        dto.setDescription(entry.getDescription());
        dto.setCalories(entry.getCalories());
        dto.setProtein(entry.getProtein());
        dto.setCarbohydrates(entry.getCarbohydrates());
        dto.setFat(entry.getFat());
        dto.setMicronutrients(entry.getMicronutrients().stream().map(this::mapMicronutrient).toList());
        return dto;
    }

    private MicronutrientResponse mapMicronutrient(NutritionMicronutrient micronutrient) {
        MicronutrientResponse dto = new MicronutrientResponse();
        dto.setId(micronutrient.getId());
        dto.setName(micronutrient.getName());
        dto.setAmount(micronutrient.getAmount());
        dto.setUnit(micronutrient.getUnit());
        return dto;
    }

    private void validate(NutritionEntryRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nutrition entry is required");
        }
        if (request.getMealName() == null || request.getMealName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meal name is required");
        }
        validateNonNegative(request.getCalories(), "Calories");
        validateNonNegative(request.getProtein(), "Protein");
        validateNonNegative(request.getCarbohydrates(), "Carbohydrates");
        validateNonNegative(request.getFat(), "Fat");
    }

    private void validateMicronutrient(MicronutrientRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Micronutrient name is required");
        }
        if (request.getUnit() == null || request.getUnit().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Micronutrient unit is required");
        }
        validateNonNegative(request.getAmount(), "Micronutrient amount");
    }

    private void validateNonNegative(Number value, String fieldName) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        if (value.doubleValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " cannot be negative");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
