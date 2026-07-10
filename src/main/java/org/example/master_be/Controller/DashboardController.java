package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.DashboardSummaryResponse;
import org.example.master_be.Service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;
    private final AuthUtil authUtil;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(@RequestParam(required = false) LocalDate from,
                                               @RequestParam(required = false) LocalDate to) {
        return service.getSummary(authUtil.getCurrentUserId(), from, to);
    }
}
