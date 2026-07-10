package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.HydrationEntryRequest;
import org.example.master_be.DTO.HydrationEntryResponse;
import org.example.master_be.DTO.HydrationSummaryResponse;
import org.example.master_be.Service.HydrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hydration")
@RequiredArgsConstructor
public class HydrationController {

    private final HydrationService service;
    private final AuthUtil authUtil;

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public HydrationEntryResponse createEntry(@RequestBody HydrationEntryRequest request) {
        return service.createEntry(authUtil.getCurrentUserId(), request);
    }

    @GetMapping("/entries")
    public List<HydrationEntryResponse> getEntries() {
        return service.getEntries(authUtil.getCurrentUserId());
    }

    @GetMapping("/today")
    public HydrationSummaryResponse getTodaySummary() {
        return service.getTodaySummary(authUtil.getCurrentUserId());
    }

    @GetMapping("/day")
    public HydrationSummaryResponse getDaySummary(@RequestParam(required = false) LocalDate date) {
        return service.getDaySummary(authUtil.getCurrentUserId(), date);
    }

    @GetMapping("/average/week")
    public HydrationSummaryResponse getWeekAverage() {
        return service.getWeekAverage(authUtil.getCurrentUserId());
    }

    @GetMapping("/average/month")
    public HydrationSummaryResponse getMonthAverage() {
        return service.getMonthAverage(authUtil.getCurrentUserId());
    }
}
