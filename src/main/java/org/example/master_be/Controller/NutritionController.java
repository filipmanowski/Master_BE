package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.NutritionEntryRequest;
import org.example.master_be.DTO.NutritionEntryResponse;
import org.example.master_be.DTO.NutritionSummaryResponse;
import org.example.master_be.Service.NutritionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService service;
    private final AuthUtil authUtil;

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public NutritionEntryResponse createEntry(@RequestBody NutritionEntryRequest request) {
        return service.createEntry(authUtil.getCurrentUserId(), request);
    }

    @GetMapping("/entries")
    public List<NutritionEntryResponse> getEntries() {
        return service.getEntries(authUtil.getCurrentUserId());
    }

    @GetMapping("/summary/day")
    public NutritionSummaryResponse getDailySummary(@RequestParam(required = false) LocalDate date) {
        return service.getDailySummary(authUtil.getCurrentUserId(), date);
    }

    @DeleteMapping("/entries/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long id) {
        service.deleteEntry(id, authUtil.getCurrentUserId());
    }
}
