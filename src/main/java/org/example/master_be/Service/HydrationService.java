package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.HydrationEntryRequest;
import org.example.master_be.DTO.HydrationEntryResponse;
import org.example.master_be.DTO.HydrationSummaryResponse;
import org.example.master_be.Model.HydrationEntry;
import org.example.master_be.Model.User;
import org.example.master_be.Repository.HydrationEntryRepository;
import org.example.master_be.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HydrationService {

    private final HydrationEntryRepository hydrationRepo;
    private final UserRepository userRepo;

    public HydrationEntryResponse createEntry(Long userId, HydrationEntryRequest request) {
        validate(request);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        HydrationEntry entry = new HydrationEntry();
        entry.setUser(user);
        entry.setDrankAt(request.getDrankAt() == null ? LocalDateTime.now() : request.getDrankAt());
        entry.setAmountMl(request.getAmountMl());
        return mapToDto(hydrationRepo.save(entry));
    }

    public List<HydrationEntryResponse> getEntries(Long userId) {
        return hydrationRepo.findByUserIdOrderByDrankAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public HydrationSummaryResponse getTodaySummary(Long userId) {
        return getDaySummary(userId, LocalDate.now());
    }

    public HydrationSummaryResponse getDaySummary(Long userId, LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return summarize(userId, targetDate, targetDate.plusDays(1), 1);
    }

    public HydrationSummaryResponse getWeekAverage(Long userId) {
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDate from = to.minusDays(7);
        return summarize(userId, from, to, 7);
    }

    public HydrationSummaryResponse getMonthAverage(Long userId) {
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDate from = to.minusMonths(1);
        return summarize(userId, from, to, java.time.temporal.ChronoUnit.DAYS.between(from, to));
    }

    private HydrationSummaryResponse summarize(Long userId, LocalDate from, LocalDate toExclusive, long days) {
        List<HydrationEntry> entries = hydrationRepo.findByUserIdAndDrankAtBetweenOrderByDrankAtAsc(
                userId,
                from.atStartOfDay(),
                toExclusive.atStartOfDay()
        );
        int total = entries.stream().mapToInt(HydrationEntry::getAmountMl).sum();

        HydrationSummaryResponse response = new HydrationSummaryResponse();
        response.setFrom(from);
        response.setTo(toExclusive.minusDays(1));
        response.setTotalMl(total);
        response.setAverageMl(days == 0 ? 0.0 : total / (double) days);
        return response;
    }

    private HydrationEntryResponse mapToDto(HydrationEntry entry) {
        HydrationEntryResponse dto = new HydrationEntryResponse();
        dto.setId(entry.getId());
        dto.setDrankAt(entry.getDrankAt());
        dto.setAmountMl(entry.getAmountMl());
        return dto;
    }

    private void validate(HydrationEntryRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hydration entry is required");
        }
        if (request.getAmountMl() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount in ml is required");
        }
        if (request.getAmountMl() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount in ml must be positive");
        }
    }
}
